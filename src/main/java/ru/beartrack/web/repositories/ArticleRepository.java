package ru.beartrack.web.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.beartrack.web.models.Article;

import java.util.UUID;

public interface ArticleRepository extends R2dbcRepository<Article, UUID> {
}
