package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.LocationDTO;
import ru.beartrack.web.dto.LocationTypeDTO;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.models.Location;
import ru.beartrack.web.models.LocationType;
import ru.beartrack.web.services.LocationService;

import java.util.UUID;

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

    @GetMapping("/type/get/all")
    public Flux<LocationType> typeGetAll(){
        return locationService.getAllLocationTypes();
    }

    @PostMapping("/type/add")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Boolean> addLocationType(@ModelAttribute LocationTypeDTO locationTypeDTO){
        return locationService.saveLocationType(locationTypeDTO).flatMap(locationType -> {
            log.info("location type has been saved {}", locationType);
            return Mono.just(true);
        }).switchIfEmpty(Mono.just(false));
    }

    @GetMapping("/get/all")
    public Flux<Location> getAllLocation(){
        return locationService.getAll();
    }

    @GetMapping("/user/get/all")
    public Flux<Location> getAllByUser(@RequestParam(name = "user") String userUUID){
        if(userUUID.equals("0")){
            log.info("This is all {}", userUUID);
            return locationService.getAll();
        }else{
            log.info("personal {}", userUUID);
            UUID uuid = UUID.fromString(userUUID);
            return locationService.getAllByUserUuid(uuid);
        }
    }

    @GetMapping("/synchronise")
    public Mono<Boolean> synchroniseData(@AuthenticationPrincipal ApplicationUser user, @RequestParam(name = "domain") String domain){
        WebClient webClient = WebClient.builder()
                .baseUrl("https://" + domain)
                .build();

        return webClient.get()
                .uri("/api/location/get/all")
                .retrieve()
                .bodyToFlux(Location.class)
                .flatMap(location -> {
                    return locationService.synchronise(location,user);
                })
                .collectList()
                .flatMap(l -> {
                    log.info("all data synchronize [{}]",l);
                    return Mono.just(true);
                });
    }

    @GetMapping("/manticore/indexation")
    public Mono<String> indexation(){
        return locationService.manticoreIndexation().flatMap(location -> {
            log.info("location {} is indexation", location);
            return Mono.just(location);
        }).collectList().flatMap(l -> Mono.just("indexing of " + l.size() + " locations data"));
    }
}
