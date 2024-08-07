package ru.beartrack.web.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.Location;

import java.util.UUID;

public interface LocationRepository extends R2dbcRepository<Location, UUID> {
    Mono<Location> findByUuid(UUID uuid);
    Mono<Location> findBySef(String sef);

    Flux<Location> findAllByCreator(UUID uuid);
    Flux<Location> findAllByOrderByCreatedDesc();

    Flux<Location> findAllByOrderByCreatedDesc(Pageable pageable);
    Flux<Location> findAllByOrderByCountDesc();
    Flux<Location> findAllByOrderByCountDesc(Pageable pageable);
}
