package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class LocationDTO {
    private String title;
    private String notation;
    private String latitude;
    private String longitude;
    private UUID subject;
    private String metaDescription;
    private String metaKeywords;
    private List<ContentDTO> blocks = new ArrayList<>();
}
