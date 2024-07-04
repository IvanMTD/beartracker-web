package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.SubjectDTO;
import ru.beartrack.web.models.Subject;
import ru.beartrack.web.repositories.SubjectRepository;

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
}
