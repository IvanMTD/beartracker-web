package ru.beartrack.web.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.ApplicationUser;

@Repository
public interface ApplicationUserRepository extends ReactiveCrudRepository<ApplicationUser,Long> {
    Mono<ApplicationUser> findByUsername(String username);
}
