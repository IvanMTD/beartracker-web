package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.beartrack.web.enums.ContentType;
import ru.beartrack.web.models.Location;
import ru.beartrack.web.models.LocationContent;
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
    public Mono<Rendering> locationListPage(@RequestParam(name = "page") int page){
        return locationService.getCount().flatMap(locationCount -> {
            int pageSize = 12;
            int pageControl = page;
            long lastPage = locationCount / pageSize;
            long dif = (locationCount % pageSize);
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
            return locationService.getAllOrderByCount(PageRequest.of(finalPageControl,pageSize)).collectList().flatMap(locations -> {
                String description = "Интересные места на странице: ";
                for(Location location : locations){
                    description += location.getTitle() + ", ";
                }
                description = description.substring(0, description.length() - 2);
                return Mono.just(
                        Rendering.view("template")
                                .modelAttribute("title","Интересные места страница " + finalPageControl)
                                .modelAttribute("index","location-list-page")
                                .modelAttribute("metaDescription", description)
                                .modelAttribute("page", finalPageControl)
                                .modelAttribute("lastPage", finalLastPage)
                                .modelAttribute("posts", locations)
                                .build()
                );
            });
        });
    }

    @GetMapping("/{sef}")
    public Mono<Rendering> locationShowPage(@PathVariable String sef){
        return locationService.getBySef(sef).flatMap(location -> subjectService.getByUuid(location.getSubject()).flatMap(subject -> {
            location.setSubjectModel(subject);
            String imageUrl = "";
            for(LocationContent lc : location.getContentList()){
                if(lc.getImageUrlLg() != null ){
                    if(!lc.getImageUrlLg().equals("")){
                        imageUrl = lc.getImageUrlLg();
                        break;
                    }
                }
            }
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title",location.getMetaTitle())
                            .modelAttribute("index","location-show-page")
                            .modelAttribute("metaDescription",location.getMetaDescription())
                            .modelAttribute("metaKeywords",location.getKeywords())
                            .modelAttribute("post",location)
                            .modelAttribute("nearestLocations", locationService.getLocationsNear(location,100.0))
                            .modelAttribute("ogUrl","/location/" + sef)
                            .modelAttribute("ogType","article")
                            .modelAttribute("ogImage",imageUrl)
                            .modelAttribute("ogLogo","/img/logo.svg")
                            .build()
            );
        }));
    }

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public Mono<Rendering> locationCreatePage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Добавление нового места")
                        .modelAttribute("index","location-create-page")
                        .modelAttribute("types", ContentType.valuesOfDTO())
                        .modelAttribute("subjects", subjectService.getAll())
                        .modelAttribute("locationTypes",locationService.getAllLocationTypes())
                        .build()
        );
    }

    @GetMapping("/edit/{sef}")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public Mono<Rendering> locationEditPage(@PathVariable String sef){
        return locationService.getBySef(sef).flatMap(location -> subjectService.getByUuid(location.getSubject()).flatMap(subject -> {
            location.setSubjectModel(subject);
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Редактор локаций")
                            .modelAttribute("index","location-edit-page")
                            .modelAttribute("types", ContentType.valuesOfDTO())
                            .modelAttribute("subjects", subjectService.getAll())
                            .modelAttribute("locationTypes",locationService.getAllLocationTypes())
                            .modelAttribute("post",location)
                            .build()
            );
        }));
    }

    @GetMapping("/delete/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public Mono<Rendering> deleteLocationPost(@PathVariable UUID uuid){
        return locationService.delete(uuid).flatMap(location -> {
            log.info("location has been deleted [{}]",location);
            return Mono.just(Rendering.redirectTo("/account/personal").build());
        });
    }
}
