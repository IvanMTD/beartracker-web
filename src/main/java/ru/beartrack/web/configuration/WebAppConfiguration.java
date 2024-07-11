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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.SubjectRecord;
import ru.beartrack.web.enums.FederalDistrict;
import ru.beartrack.web.enums.Role;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.models.Subject;
import ru.beartrack.web.repositories.ApplicationUserRepository;
import ru.beartrack.web.repositories.SubjectRepository;
import ru.beartrack.web.utils.CookieUtil;
import ru.beartrack.web.utils.SubjectRepoUtil;

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
    public CommandLineRunner prepare(ApplicationUserRepository userRepository, PasswordEncoder passwordEncoder, SubjectRepository subjectRepository){
        return args -> {
            log.info("***************** environments start *******************");
            for(String key : System.getenv().keySet()){
                String[] p = key.split("\\.");
                if(p.length > 1) {
                    log.info("\"{}\"=\"{}\"", key, System.getenv().get(key));
                }
            }
            log.info("****************** environments end ********************");
            setupSubject(subjectRepository).collectList().flatMap(subjects -> {
                log.info("{} records of subjects in the database",subjects.size());
                return userRepository.findByUsername(username).flatMap(existingUser  -> {
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
                );
            }).subscribe();
        };
    }

    private Flux<Subject> setupSubject(SubjectRepository subjectRepository){
        return Flux.fromIterable(SubjectRepoUtil.getInstance().getSubjectRecords()).flatMap(subjectRecord -> {
            return subjectRepository.findByIso(subjectRecord.ISO()).flatMap(subject -> {
                subject.setTitle(subjectRecord.name());
                subject.setFederalDistrict(FederalDistrict.valueOf(subjectRecord.federalDistrict()));
                return subjectRepository.save(subject);
            }).switchIfEmpty(Mono.just(new Subject()).flatMap(subject -> {
                subject.setTitle(subjectRecord.name());
                subject.setIso(subjectRecord.ISO());
                subject.setFederalDistrict(FederalDistrict.valueOf(subjectRecord.federalDistrict()));
                return subjectRepository.save(subject);
            }));
        });
    }
}
