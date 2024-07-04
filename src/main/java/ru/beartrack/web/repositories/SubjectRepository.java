package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.beartrack.web.models.Subject;

import java.util.UUID;

public interface SubjectRepository extends R2dbcRepository<Subject, UUID> {
}
