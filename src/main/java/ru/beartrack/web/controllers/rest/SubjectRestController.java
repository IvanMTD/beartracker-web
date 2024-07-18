package ru.beartrack.web.controllers.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.SubjectDTO;
import ru.beartrack.web.models.Subject;
import ru.beartrack.web.services.SubjectService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subject")
public class SubjectRestController {

    private final SubjectService subjectService;

    @GetMapping("/synchronise")
    public Mono<Boolean> synchroniseData(@RequestParam(name = "server") String server, @RequestParam(name = "port") String port){
        WebClient webClient = WebClient.builder()
                .baseUrl("http://" + server + ":" + port)
                .build();

        return webClient.get()
                .uri("/api/subject/get/all")
                .retrieve()
                .bodyToFlux(SubjectDTO.class)
                .flatMap(subjectDTO -> {
                    log.info("found subject [{}]",subjectDTO);
                    return subjectService.synchronizeSubject(subjectDTO);
                })
                .collectList()
                .flatMap(l -> {
                    log.info("all data synchronize [{}]",l);
                    return Mono.just(true);
                });
    }

    @GetMapping("/get/all")
    public Flux<Subject> getAllSubject(){
        return subjectService.getAll();
    }
}
