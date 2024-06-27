package ru.beartrack.web.configuration.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.beartrack.web.services.ApplicationUserService;
import ru.beartrack.web.services.JwtService;
import ru.beartrack.web.utils.CookieUtil;

import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwt;
    private final ApplicationUserService userService;
    private final PasswordEncoder encoder;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        log.info("start auth manager");
        String accessToken = authentication.getPrincipal().toString();
        String refreshToken = authentication.getCredentials().toString();

        if(jwt.validateToken(accessToken)){
            log.info("access token is valid");
            return Mono.deferContextual(contextView -> {
                ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
                String digitalSignature = exchange.getRequest().getHeaders().getFirst("User-Agent");
                assert digitalSignature != null;
                if(digitalSignature.equals(jwt.getDigitalSignatureFromToken(accessToken))) {
                    log.info("the correct digital signature has been presented");
                    return userService.findByUsername(jwt.getUsernameFromToken(accessToken)).flatMap(user -> Mono.just(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities())));
                }else{
                    log.error("the digital signature does not match the signature in the token [{}] | [{}]", digitalSignature, jwt.getDigitalSignatureFromToken(accessToken));
                    ServerHttpResponse response = exchange.getResponse();
                    response.addCookie(ResponseCookie.from(CookieUtil.getInstance().getACCESS(), "")
                            .httpOnly(true)
                            .path("/")
                            .maxAge(0)
                            .build());
                    response.addCookie(ResponseCookie.from(CookieUtil.getInstance().getREFRESH(), "")
                            .httpOnly(true)
                            .path("/")
                            .maxAge(0)
                            .build());
                    response.addCookie(ResponseCookie.from(CookieUtil.getInstance().getSESSION(), "")
                            .httpOnly(true)
                            .path("/")
                            .maxAge(0)
                            .build());
                    return Mono.error(new BadCredentialsException("Authentication failed"));
                }
            });
        }else if(jwt.validateToken(refreshToken)){
            log.info("access token is not valid, but refresh token is ok");
            return Mono.deferContextual(contextView -> {
                ServerWebExchange exchange = contextView.get(ServerWebExchange.class);
                String digitalSignature = exchange.getRequest().getHeaders().getFirst("User-Agent");
                assert digitalSignature != null;
                if(digitalSignature.equals(jwt.getDigitalSignatureFromToken(refreshToken))) {
                    log.info("digital signature of refresh token is ok - update jwt");
                    return userService.findByUsername(jwt.getUsernameFromToken(refreshToken)).flatMap(user -> {
                        String username = user.getUsername();
                        String newAccessToken = jwt.generateAccessToken(username, digitalSignature);
                        String newRefreshToken = jwt.generateRefreshToken(username, digitalSignature);
                        ResponseCookie accessCookie = ResponseCookie.from(CookieUtil.getInstance().getACCESS(), newAccessToken)
                                .httpOnly(true)
                                .maxAge(Duration.ofMillis(jwt.getAccessExpiration()))
                                .path("/")
                                .build();
                        ResponseCookie refreshCookie = ResponseCookie.from(CookieUtil.getInstance().getREFRESH(), newRefreshToken)
                                .httpOnly(true)
                                .maxAge(Duration.ofMillis(jwt.getAccessExpiration()))
                                .path("/")
                                .build();

                        exchange.getResponse().addCookie(accessCookie);
                        exchange.getResponse().addCookie(refreshCookie);
                        return Mono.just(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
                    }).switchIfEmpty(Mono.error(new BadCredentialsException("Authentication failed")));
                }else{
                    log.error("the digital signature of refresh token does not match the signature in the token [{},{}]", digitalSignature, jwt.getDigitalSignatureFromToken(refreshToken));
                    return Mono.error(new BadCredentialsException("Authentication failed"));
                }
            });
        }else{
            return baseAuth(authentication);
        }
    }

    private Mono<Authentication> baseAuth(Authentication authentication){
        log.info("access and refresh token is not valid - try authenticate by basic login");
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        return userService.findByUsername(username).flatMap(user -> {
            if(encoder.matches(password,user.getPassword())){
                return Mono.just(new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities()));
            } else{
                return Mono.error(new BadCredentialsException("Authentication failed"));
            }
        }).cast(Authentication.class).switchIfEmpty(Mono.error(new BadCredentialsException("Authentication failed")));
    }
}
