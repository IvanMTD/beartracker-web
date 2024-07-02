package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.beartrack.web.models.Location;

import java.util.UUID;

public interface LocationRepository extends R2dbcRepository<Location, UUID> {
}
