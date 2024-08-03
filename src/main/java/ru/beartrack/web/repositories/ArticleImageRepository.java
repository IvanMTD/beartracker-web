package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.beartrack.web.models.ArticleImage;

import java.util.UUID;

public interface ArticleImageRepository extends R2dbcRepository<ArticleImage, UUID> {
    Flux<ArticleImage> findByParent(UUID uuid);
}
