package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.beartrack.web.services.ApplicationUserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private final ApplicationUserService userService;
    @GetMapping("/search/username")
    public Mono<Boolean> isUsernameExist(@RequestParam(name = "request") String username){
        return userService.findByUsername(username).flatMap(userDetails -> Mono.just(true)).switchIfEmpty(Mono.just(false));
    }
}
