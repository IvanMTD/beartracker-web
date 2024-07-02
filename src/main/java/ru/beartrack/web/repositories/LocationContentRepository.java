package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.beartrack.web.models.LocationContent;

import java.util.UUID;

public interface LocationContentRepository extends R2dbcRepository<LocationContent, UUID> {
}
