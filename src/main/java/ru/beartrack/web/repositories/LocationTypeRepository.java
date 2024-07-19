package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.beartrack.web.models.LocationType;

import java.util.UUID;

public interface LocationTypeRepository extends R2dbcRepository<LocationType, UUID> {
}
