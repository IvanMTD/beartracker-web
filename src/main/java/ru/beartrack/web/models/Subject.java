package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.beartrack.web.dto.SubjectDTO;
import ru.beartrack.web.enums.FederalDistrict;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Subject {
    @Id
    private UUID uuid;

    private String title;
    private String iso;
    private FederalDistrict federalDistrict;

    public Subject(SubjectDTO subjectDTO) {
        setTitle(subjectDTO.getTitle());
        setIso(subjectDTO.getIso());
        setFederalDistrict(subjectDTO.getFederalDistrict());
    }
}
