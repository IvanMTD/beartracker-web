package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.beartrack.web.enums.FederalDistrict;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class SubjectDTO {
    private int id;
    private String title;
    private String iso;
    private FederalDistrict federalDistrict;
    private Set<Integer> sportSchoolIds = new HashSet<>();
    private Set<Integer> baseSportIds = new HashSet<>();
}
