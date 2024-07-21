package ru.beartrack.web.repositories;

import org.reactivestreams.Publisher;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.Location;
import ru.beartrack.web.models.LocationType;

import java.util.UUID;

public interface LocationTypeRepository extends R2dbcRepository<LocationType, UUID> {
    Mono<LocationType> findByUuid(UUID uuid);
}
