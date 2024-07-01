package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.PersonDTO;
import ru.beartrack.web.services.ApplicationUserService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController {
    private final ApplicationUserService userService;
    @GetMapping("/search/username")
    public Mono<Boolean> isUsernameExist(@RequestParam(name = "username") String username){
        return userService.findByUsername(username).flatMap(userDetails -> Mono.just(true)).switchIfEmpty(Mono.just(false));
    }

    @GetMapping("/user/email/check")
    public Mono<Boolean> isEmailExist(@RequestParam(name = "email") String email){
        return userService.findByEmail(email).flatMap(user -> Mono.just(true)).switchIfEmpty(Mono.just(false));
    }

    @PostMapping("/reg/user")
    public Mono<Boolean> regUser(@RequestBody PersonDTO personDTO){
        return userService.registrationUser(personDTO).flatMap(user -> {
            log.info("user has been registered [{}]",user);
            return Mono.just(true);
        }).switchIfEmpty(Mono.just(false));
    }
}
