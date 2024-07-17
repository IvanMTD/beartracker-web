package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;
import ru.beartrack.web.services.SitemapService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/sitemap.xml")
public class SitemapController {

    private final SitemapService sitemapService;

    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    @ResponseBody
    public Flux<String> getSitemap() {
        return Flux.concat(
                Flux.just("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"),
                Flux.just("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"),
                Flux.just(
                        "<url>\n" +
                        "<loc>https://beartrack.ru/</loc>\n" +
                        "<lastmod>2024-07-10</lastmod>\n" +
                        "<changefreq>monthly</changefreq>\n" +
                        "<priority>0.8</priority>\n" +
                        "</url>"
                ),
                sitemapService.getUrlData().flatMap(data -> Flux.just("<url>", data, "</url>")),
                Flux.just("</urlset>")
        );
    }
}
