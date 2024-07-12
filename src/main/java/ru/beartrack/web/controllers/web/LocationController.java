package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.beartrack.web.enums.ContentType;
import ru.beartrack.web.services.LocationService;
import ru.beartrack.web.services.SubjectService;

import java.util.UUID;

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
                            .modelAttribute("title",location.getMetaTitle())
                            .modelAttribute("index","location-show-page")
                            .modelAttribute("metaDescription",location.getMetaDescription())
                            .modelAttribute("metaKeywords",location.stringKeywords())
                            .modelAttribute("post",location)
                            .build()
            );
        }));
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
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

    @GetMapping("/edit/{sef}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Rendering> locationEditPage(@PathVariable String sef){
        return locationService.getBySef(sef).flatMap(location -> subjectService.getByUuid(location.getSubject()).flatMap(subject -> {
            location.setSubjectModel(subject);
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Редактор локаций")
                            .modelAttribute("index","location-edit-page")
                            .modelAttribute("types", ContentType.valuesOfDTO())
                            .modelAttribute("subjects", subjectService.getAll())
                            .modelAttribute("post",location)
                            .build()
            );
        }));
    }

    @GetMapping("/delete/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Rendering> deleteLocationPost(@PathVariable UUID uuid){
        return locationService.delete(uuid).flatMap(location -> {
            log.info("location has been deleted [{}]",location);
            return Mono.just(Rendering.redirectTo("/account/personal").build());
        });
    }
}
