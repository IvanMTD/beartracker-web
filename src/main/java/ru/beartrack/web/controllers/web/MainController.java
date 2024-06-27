package ru.beartrack.web.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
public class MainController {
    @GetMapping("/")
    public Mono<Rendering> homePage(){
        return Mono.just(
                Rendering.view("template")
                        .modelAttribute("title","Главная страница")
                        .modelAttribute("index","main-page")
                        .build()
        );
    }
}
