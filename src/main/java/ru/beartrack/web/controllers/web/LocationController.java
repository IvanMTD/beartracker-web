package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/location")
public class LocationController {
    @GetMapping("/list")
    public Mono<Rendering> locationListPage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Интересные места")
                        .modelAttribute("index","location-list-page")
                        .build()
        );
    }
}
