package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.GlobalSearchDTO;
import ru.beartrack.web.services.LocationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/global")
public class GlobalSearchRestController {

    private final LocationService locationService;

    @GetMapping("/search")
    public Mono<GlobalSearchDTO> globalSearch(@RequestParam(name = "query") String query){
        log.info("INCOMING QUERY {}",query);
        return locationService.globalSearch(query).collectList().flatMap(l -> {
            GlobalSearchDTO globalSearch = new GlobalSearchDTO();
            globalSearch.setLocations(l);
            return Mono.just(globalSearch);
        });
    }
}
