package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class LocationDTO {
    private String title;
    private String notation;
    private String latitude;
    private String longitude;
    private List<ContentDTO> blocks = new ArrayList<>();
}
