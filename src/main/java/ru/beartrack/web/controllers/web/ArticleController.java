package ru.beartrack.web.controllers.web;

import com.nimbusds.jose.util.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.beartrack.web.models.Article;
import ru.beartrack.web.models.ArticleImage;
import ru.beartrack.web.models.Location;
import ru.beartrack.web.models.LocationContent;
import ru.beartrack.web.services.ArticleService;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/create")
    public Mono<Rendering> articleCreatePage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Написать статью")
                        .modelAttribute("index","article-create-page")
                        .build()
        );
    }

    @GetMapping("/edit/{sef}")
    public Mono<Rendering> articleEditPage(@PathVariable String sef){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Редактор стати")
                        .modelAttribute("index","article-edit-page")
                        .modelAttribute("article",articleService.getBySef(sef))
                        .build()
        );
    }

    @GetMapping("/list")
    public Mono<Rendering> articleListPage(@RequestParam(name = "page") int page){
        return articleService.getCount().flatMap(articleCount -> {
            int pageSize = 12;
            int pageControl = page;
            long lastPage = articleCount / pageSize;
            long dif = (articleCount % pageSize);
            if(dif == 0){
                lastPage = lastPage - 1;
                if(lastPage < 0){
                    lastPage = 0;
                }
            }
            if(pageControl <= 0){
                pageControl = 0;
            }
            if(pageControl >= lastPage){
                pageControl = (int)lastPage;
            }

            int finalPageControl = pageControl;
            long finalLastPage = lastPage;

            return articleService.getAllOrderByDate(PageRequest.of(finalPageControl,pageSize)).collectList().flatMap(articles -> {
                String description = "Стетьи на странице: ";
                for(Article article : articles){
                    description += article.getTitle() + ", ";
                }
                description = description.substring(0, description.length() - 2);
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Интересные статьи, страница " + finalPageControl)
                                .modelAttribute("index","article-list-page")
                                .modelAttribute("metaDescription", description)
                                .modelAttribute("page", finalPageControl)
                                .modelAttribute("lastPage", finalLastPage)
                                .modelAttribute("articles", articles)
                                .build()
                );
            });
        });
    }

    @GetMapping("/{sef}")
    public Mono<Rendering> articleShow(@PathVariable String sef){
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

    @GetMapping("/delete/{uuid}")
    public Mono<Rendering> articleDelete(@PathVariable UUID uuid){
        return articleService.deleteByUuid(uuid).flatMap(article -> {
            log.info("article has been deleted from db: {}",article);
            return Mono.just(Rendering.redirectTo("/account/personal").build());
        });
    }
}
