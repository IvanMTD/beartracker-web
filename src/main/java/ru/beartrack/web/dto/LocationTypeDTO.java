package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@NoArgsConstructor
public class LocationTypeDTO {
    private String name;
    private String description;
    private FilePart image;
}
