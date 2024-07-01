package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.ApplicationUser;

import java.util.UUID;

public interface ApplicationUserRepository extends R2dbcRepository<ApplicationUser, UUID> {
    Mono<ApplicationUser> findByUsername(String username);
    Mono<ApplicationUser> findByEmail(String email);
}
