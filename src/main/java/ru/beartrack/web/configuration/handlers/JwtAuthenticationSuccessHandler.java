package ru.beartrack.web.configuration.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import reactor.core.publisher.Mono;
import ru.beartrack.web.services.JwtService;
import ru.beartrack.web.utils.CookieUtil;

import java.net.URI;
import java.time.Duration;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtService jwt;
    private final ServerRequestCache requestCache;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("auth login success");
        String username = authentication.getName();
        String digitalSignature = webFilterExchange.getExchange().getRequest().getHeaders().getFirst("User-Agent");
        String accessToken = jwt.generateAccessToken(username, digitalSignature);
        String refreshToken = jwt.generateRefreshToken(username, digitalSignature);
        ResponseCookie accessCookie = ResponseCookie.from(CookieUtil.getInstance().getACCESS(), accessToken)
                .httpOnly(true)
                .maxAge(Duration.ofMillis(jwt.getAccessExpiration()))
                .path("/")
                .build();
        ResponseCookie refreshCookie = ResponseCookie.from(CookieUtil.getInstance().getREFRESH(), refreshToken)
                .httpOnly(true)
                .maxAge(Duration.ofMillis(jwt.getRefreshExpiration()))
                .path("/")
                .build();

        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return requestCache.getRedirectUri(webFilterExchange.getExchange())
                .defaultIfEmpty(URI.create("/"))
                .flatMap(redirectUri -> {
                    response.setStatusCode(HttpStatus.FOUND);
                    webFilterExchange.getExchange().getResponse().getHeaders().setLocation(redirectUri);
                    return webFilterExchange.getExchange().getResponse().setComplete();
                });
    }
}
