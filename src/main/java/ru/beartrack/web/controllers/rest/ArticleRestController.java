package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.ArticleDTO;
import ru.beartrack.web.services.ArticleService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
public class ArticleRestController {
    private final ArticleService articleService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public Mono<Boolean> articleCreate(@ModelAttribute ArticleDTO articleDTO){
        log.info("incoming article: {}", articleDTO);
        return Mono.just(true);
    }

}
