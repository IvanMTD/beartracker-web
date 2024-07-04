package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.Subject;

import java.util.UUID;

public interface SubjectRepository extends R2dbcRepository<Subject, UUID> {
    Mono<Subject> findByIso(String iso);
}
