package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.beartrack.web.enums.ContentType;
import ru.beartrack.web.services.LocationService;
import ru.beartrack.web.services.SubjectService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/location")
public class LocationController {

    private final SubjectService subjectService;
    private final LocationService locationService;

    @GetMapping("/list")
    public Mono<Rendering> locationListPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Интересные места")
                        .modelAttribute("index","location-list-page")
                        .modelAttribute("posts", locationService.getAll().flatMap(location -> subjectService.getByUuid(location.getSubject()).flatMap(subject -> {
                            location.setSubjectModel(subject);
                            return Mono.just(location);
                        })))
                        .build()
        );
    }

    @GetMapping("/{sef}")
    public Mono<Rendering> locationShowPage(@PathVariable String sef){
        return locationService.getBySef(sef).flatMap(location -> subjectService.getByUuid(location.getSubject()).flatMap(subject -> {
            location.setSubjectModel(subject);
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title",location.getTitle())
                            .modelAttribute("index","location-show-page")
                            .modelAttribute("description",location.getMetaDescription())
                            .modelAttribute("post",location)
                            .build()
            );
        }));
    }

    @GetMapping("/create")
    public Mono<Rendering> locationCreatePage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Добавление нового места")
                        .modelAttribute("index","location-create-page")
                        .modelAttribute("types", ContentType.valuesOfDTO())
                        .modelAttribute("subjects", subjectService.getAll())
                        .build()
        );
    }
}
