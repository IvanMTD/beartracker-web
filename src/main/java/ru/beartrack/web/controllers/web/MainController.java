package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
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
                        .modelAttribute("title","Главная страница")
                        .modelAttribute("index","main-page")
                        .modelAttribute("posts", locationService.getAllOrderByCreated().flatMap(location -> subjectService.getByUuid(location.getSubject()).flatMap(subject -> {
                            location.setSubjectModel(subject);
                            return Mono.just(location);
                        })).take(8))
                        .build()
        );
    }
}
