package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.services.LocationService;
import ru.beartrack.web.services.SubjectService;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final LocationService locationService;
    private final SubjectService subjectService;

    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","BearTrack: маршруты для самостоятельных путешествий")
                        .modelAttribute("index","main-page")
                        .modelAttribute("metaDescription","BearTrack – ваш путеводитель по самостоятельным путешествиям. Открывайте лучшие маршруты, карты и советы для активного отдыха. Путешествуйте легко и безопасно с BearTrack!")
                        .modelAttribute("metaKeywords","камчатка, тропы, экотропы, туризм, вулканы")
                        .modelAttribute("posts", locationService.getAllOrderByCount().flatMapSequential(location -> subjectService.getByUuid(location.getSubject()).flatMap(subject -> {
                            location.setSubjectModel(subject);
                            return Mono.just(location);
                        })))
                        .build()
        );
    }
}
