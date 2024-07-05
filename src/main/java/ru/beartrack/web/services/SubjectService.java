package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.SubjectDTO;
import ru.beartrack.web.models.Subject;
import ru.beartrack.web.repositories.SubjectRepository;

import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {
    private final SubjectRepository subjectRepository;

    public Mono<Subject> synchronizeSubject(SubjectDTO subjectDTO) {
        return subjectRepository.findByIso(subjectDTO.getIso()).flatMap(subject -> {
            subject.setTitle(subjectDTO.getTitle());
            subject.setFederalDistrict(subjectDTO.getFederalDistrict());
            return subjectRepository.save(subject);
        }).switchIfEmpty(Mono.just(new Subject(subjectDTO)).flatMap(subjectRepository::save));
    }

    public Flux<Subject> getAll() {
        return subjectRepository.findAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Subject::getTitle)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
}
