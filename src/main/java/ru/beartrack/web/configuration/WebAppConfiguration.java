package ru.beartrack.web.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import reactor.core.publisher.Mono;
import ru.beartrack.web.enums.Role;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.repositories.ApplicationUserRepository;

import java.time.LocalDate;
import java.util.UUID;

@Slf4j
@Configuration
public class WebAppConfiguration implements WebFluxConfigurer {
    @Value("${app.admin.username}")
    private String username;
    @Value("${app.admin.password}")
    private String password;

   /* @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/node_modules/**")
                .addResourceLocations("classpath:/node_modules/");
    }*/

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
            userRepository.findByUsername(username).flatMap(user -> {
                log.info("user {} exist", user.getUsername());
                return Mono.just(user);
            }).switchIfEmpty(
                    Mono.just(new ApplicationUser()).flatMap(user -> {
                        user.setUuid(UUID.randomUUID().toString());
                        user.setUsername(username);
                        user.setPassword(passwordEncoder.encode(password));
                        user.setPlacedAt(LocalDate.now());
                        user.setRole(Role.ADMIN);
                        return userRepository.save(user).flatMap(savedUser -> {
                            log.info("user created {}", savedUser);
                            return Mono.just(savedUser);
                        });
                    })
            ).subscribe();
        };
    }
}
