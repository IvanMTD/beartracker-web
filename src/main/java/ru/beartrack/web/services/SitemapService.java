package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.repositories.ArticleRepository;
import ru.beartrack.web.repositories.LocationRepository;

@Service
@RequiredArgsConstructor
public class SitemapService {
    private final LocationRepository locationRepository;
    private final ArticleRepository articleRepository;

    public Flux<String> getLocationUrls(){
        return locationRepository.findAll().flatMap(location -> {
            String url = "https://beartrack.ru/location/" + location.getSef();
            String lastmod = "";
            if(location.getUpdated() == null){
                lastmod = location.getCreated().toString();
            }else{
                lastmod = location.getUpdated().toString();
            }
            String data =
                    "<loc>" + url + "</loc>\n" +
                    "<lastmod>" + lastmod + "</lastmod>\n" +
                    "<changefreq>monthly</changefreq>\n" +
                    "<priority>0.8</priority>";
            return Mono.just(data);
        });
    }

    public Flux<String> getArticlesUrls(){
        return articleRepository.findAll().flatMap(article -> {
            String url = "https://beartrack.ru/article/" + article.getSef();
            String lastmod = "";
            if(article.getUpdated() == null){
                lastmod = article.getCreated().toString();
            }else{
                lastmod = article.getUpdated().toString();
            }
            String data =
                    "<loc>" + url + "</loc>\n" +
                            "<lastmod>" + lastmod + "</lastmod>\n" +
                            "<changefreq>monthly</changefreq>\n" +
                            "<priority>0.8</priority>";
            return Mono.just(data);
        });
    }
}
