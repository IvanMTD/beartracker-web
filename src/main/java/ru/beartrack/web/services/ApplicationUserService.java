package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.PersonDTO;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.repositories.ApplicationUserRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class ApplicationUserService implements ReactiveUserDetailsService {
    private final ApplicationUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return userRepository.findByUsername(username).cast(UserDetails.class);
    }

    public Mono<ApplicationUser> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<ApplicationUser> registrationUser(PersonDTO personDTO) {
        return Mono.justOrEmpty(new ApplicationUser(personDTO, passwordEncoder)).flatMap(userRepository::save);
    }

    public Mono<ApplicationUser> getByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid);
    }
}
