package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.ArticleImage;
import ru.beartrack.web.models.LocationContent;
import ru.beartrack.web.services.ArticleService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/create")
    public Mono<Rendering> createArticle(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Написать статью")
                        .modelAttribute("index","article-create-page")
                        .build()
        );
    }

    @GetMapping("/{sef}")
    public Mono<Rendering> showArticle(@PathVariable String sef){
        return articleService.getBySef(sef).flatMap(article -> {
            String imageUrl = "";
            for(ArticleImage ai : article.getImages()){
                if(ai.getImageUrlLg() != null ){
                    if(!ai.getImageUrlLg().equals("")){
                        imageUrl = ai.getImageUrlLg();
                        break;
                    }
                }
            }
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title",article.getMetaTitle())
                            .modelAttribute("index","article-show-page")
                            .modelAttribute("metaDescription",article.getMetaDescription())
                            .modelAttribute("metaKeywords",article.getKeywords())
                            .modelAttribute("article",article)
                            .modelAttribute("ogUrl","/article/" + sef)
                            .modelAttribute("ogType","article")
                            .modelAttribute("ogImage",imageUrl)
                            .modelAttribute("ogLogo","/img/logo.svg")
                            .build()
            );
        });
    }
}
