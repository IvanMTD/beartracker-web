package ru.beartrack.web.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.session.CookieWebSessionIdResolver;
import org.springframework.web.server.session.WebSessionIdResolver;
import reactor.core.publisher.Mono;
import ru.beartrack.web.enums.Role;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.repositories.ApplicationUserRepository;
import ru.beartrack.web.utils.CookieUtil;

import java.time.LocalDate;

@Slf4j
@Configuration
public class WebAppConfiguration implements WebFluxConfigurer {
    @Value("${app.admin.username}")
    private String username;
    @Value("${app.admin.password}")
    private String password;

    @Bean
    public WebSessionIdResolver webSessionIdResolver() {
        CookieWebSessionIdResolver resolver = new CookieWebSessionIdResolver();
        resolver.setCookieName(CookieUtil.getInstance().getSESSION());
        return resolver;
    }

    @Bean
    public CommandLineRunner prepare(ApplicationUserRepository userRepository, PasswordEncoder passwordEncoder){
        return args -> {
            log.info("***************** environments start *******************");
            for(String key : System.getenv().keySet()){
                String[] p = key.split("\\.");
                if(p.length > 1) {
                    log.info("\"{}\"=\"{}\"", key, System.getenv().get(key));
                }
            }
            log.info("****************** environments end ********************");
            userRepository.findByUsername(username).flatMap(existingUser  -> {
                log.info("user {} exist", existingUser .getUsername());
                return Mono.just(existingUser);
            }).switchIfEmpty(
                    Mono.defer(() -> {
                        ApplicationUser newUser = new ApplicationUser();
                        newUser.setUsername(username);
                        newUser.setPassword(passwordEncoder.encode(password));
                        newUser.setPlacedAt(LocalDate.now());
                        newUser.setRole(Role.ADMIN);
                        return userRepository.save(newUser)
                                .doOnNext(savedUser -> log.info("User created {}", savedUser));
                    })
            ).subscribe();
        };
    }
}
