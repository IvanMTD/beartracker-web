package ru.beartrack.web.controllers.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;
import ru.beartrack.web.enums.Role;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.services.ApplicationUserService;
import ru.beartrack.web.services.LocationService;

@Slf4j
@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final ApplicationUserService userService;
    private final LocationService locationService;

    @GetMapping("/personal")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public Mono<Rendering> personalAccount(@AuthenticationPrincipal ApplicationUser user){
        return userService.getByUuid(user.getUuid()).flatMap(u -> {
            return Mono.just(
                    Rendering.view("template")
                            .modelAttribute("title","Аккаунт пользователя " + user.getUsername())
                            .modelAttribute("index","account-personal-page")
                            .modelAttribute("user", u)
                            .modelAttribute("locations", locationService.getAllByUserUuid(user.getUuid()))
                            .modelAttribute("roles", Role.valuesOfDTO())
                            .modelAttribute("users", userService.getAll())
                            .build()
            );
        });
    }
}
