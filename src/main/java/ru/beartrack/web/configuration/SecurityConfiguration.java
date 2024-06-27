package ru.beartrack.web.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler;
import org.springframework.security.web.server.savedrequest.ServerRequestCache;
import org.springframework.security.web.server.savedrequest.WebSessionServerRequestCache;
import ru.beartrack.web.configuration.handlers.JwtAuthenticationConverter;
import ru.beartrack.web.configuration.handlers.JwtAuthenticationManager;
import ru.beartrack.web.configuration.handlers.JwtAuthenticationSuccessHandler;
import ru.beartrack.web.configuration.handlers.JwtLogoutSuccessHandler;
import ru.beartrack.web.repositories.ApplicationUserRepository;
import ru.beartrack.web.services.ApplicationUserService;
import ru.beartrack.web.services.JwtService;

import java.net.URI;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    private final ApplicationUserRepository userRepository;
    private final JwtService jwtService;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http){
        ServerCsrfTokenRequestAttributeHandler requestHandler = new ServerCsrfTokenRequestAttributeHandler();
        requestHandler.setTokenFromMultipartDataEnabled(true);

        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(authenticationManager());
        authenticationWebFilter.setServerAuthenticationConverter(authenticationConverter());

        return http
                .csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler))
                .addFilterAt(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(auth -> auth
                        //.pathMatchers("/auth/login","/favicon.ico").permitAll()
                        .anyExchange().permitAll())
                .formLogin(loginSpec -> loginSpec.loginPage("/").authenticationSuccessHandler(authenticationSuccessHandler()))
                .logout(logoutSpec -> logoutSpec.logoutSuccessHandler(logoutSuccessHandler()))
                .requestCache(requestCacheSpec -> requestCacheSpec.requestCache(serverRequestCache()))
                .build();
    }

    @Bean
    public ServerRequestCache serverRequestCache() {
        return new WebSessionServerRequestCache();
    }

    @Bean
    public JwtAuthenticationManager authenticationManager() {
        return new JwtAuthenticationManager(jwtService, userService(), passwordEncoder());
    }

    @Bean
    public JwtAuthenticationSuccessHandler authenticationSuccessHandler(){
        return new JwtAuthenticationSuccessHandler(jwtService,serverRequestCache());
    }

    @Bean
    public JwtLogoutSuccessHandler logoutSuccessHandler(){
        JwtLogoutSuccessHandler logoutSuccessHandler = new JwtLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));
        return logoutSuccessHandler;
    }

    @Bean
    public JwtAuthenticationConverter authenticationConverter(){
        return new JwtAuthenticationConverter();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationUserService userService(){
        return new ApplicationUserService(userRepository,passwordEncoder());
    }
}
