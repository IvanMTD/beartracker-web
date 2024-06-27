package ru.beartrack.web.configuration.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import reactor.core.publisher.Mono;
import ru.beartrack.web.utils.CookieUtil;

@Slf4j
public class JwtLogoutSuccessHandler extends RedirectServerLogoutSuccessHandler {
    @Override
    public Mono<Void> onLogoutSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        log.info("auth logout success");
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
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
        return super.onLogoutSuccess(webFilterExchange, authentication);
    }
}
