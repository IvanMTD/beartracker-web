package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.beartrack.web.dto.ContentDTO;
import ru.beartrack.web.models.LocationContent;

import java.util.UUID;

public interface LocationContentRepository extends R2dbcRepository<LocationContent, UUID> {
    Flux<LocationContent> findByParent(UUID parent);
}
