package ru.beartrack.web.configuration.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.beartrack.web.utils.CookieUtil;

@Slf4j
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        log.info("start auth converter");
        String accessToken = extractTokenFromCookie(exchange.getRequest(), CookieUtil.getInstance().getACCESS());
        String refreshToken = extractTokenFromCookie(exchange.getRequest(), CookieUtil.getInstance().getREFRESH());
        if(accessToken.equals("") && refreshToken.equals("")){
            log.info("token not found");
            return Mono.empty();
        }else{
            log.info("found access: {}, and refresh: {} tokens", accessToken,refreshToken);
            return Mono.just(new UsernamePasswordAuthenticationToken(accessToken, refreshToken));
        }
    }

    private String extractTokenFromCookie(ServerHttpRequest request, String cookieName) {
        HttpCookie cookie = request.getCookies().getFirst(cookieName);
        return cookie != null ? cookie.getValue() : "";
    }
}
