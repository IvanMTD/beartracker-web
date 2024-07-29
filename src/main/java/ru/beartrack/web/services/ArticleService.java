package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.beartrack.web.repositories.ArticleRepository;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
}
