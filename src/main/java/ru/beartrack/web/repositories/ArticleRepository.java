package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.Article;

import java.util.UUID;

public interface ArticleRepository extends R2dbcRepository<Article, UUID> {
    Flux<Article> findAllByOrderByCreatedDesc();

    Flux<Article> findByCreator(UUID uuid);

    Mono<Article> findBySef(String sef);
}
