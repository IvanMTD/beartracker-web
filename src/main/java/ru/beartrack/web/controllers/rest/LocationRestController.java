package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.LocationDTO;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.models.Location;
import ru.beartrack.web.services.LocationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/location")
public class LocationRestController {

    private final LocationService locationService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public Mono<Boolean> createLocationPost(@AuthenticationPrincipal ApplicationUser user, @ModelAttribute LocationDTO locationPost) {
        return locationService.saveLocation(locationPost, user.getUuid()).flatMap(location -> {
            log.info("saved location [{}]",location);
            return Mono.just(true);
        }).onErrorReturn(false);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN','MODERATOR')")
    public Mono<Boolean> updateLocationPost(@AuthenticationPrincipal ApplicationUser user, @ModelAttribute LocationDTO locationPost) {
        return locationService.update(locationPost).onErrorReturn(false);
    }

    @GetMapping("/get/all")
    public Flux<Location> getAllLocation(){
        return locationService.getAll();
    }
}
