package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.ArticleDTO;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.services.ArticleService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/article")
public class ArticleRestController {
    private final ArticleService articleService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public Mono<ResponseEntity<String>> articleCreate(@AuthenticationPrincipal ApplicationUser user, @ModelAttribute ArticleDTO articleDTO) {
        /*return images.collectList().flatMap(fileParts -> {
            articleDTO.setImages(fileParts);
            return articleService.createArticle(articleDTO, user)
                    .flatMap(article -> {
                        log.info("article saved [{}]", article);
                        return Mono.just(ResponseEntity.ok("Article created successfully"));
                    })
                    .onErrorResume(e -> {
                        log.error("Error creating article", e);
                        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating article: " + e.getMessage()));
                    });
        });*/
        log.info("incoming data: [{}]", articleDTO);
        return Mono.just(ResponseEntity.ok("Article created successfully"));
    }

}
