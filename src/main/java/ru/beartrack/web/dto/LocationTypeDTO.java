package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.util.UUID;

@Data
@NoArgsConstructor
public class LocationTypeDTO {
    private String uuid;
    private String name;
    private String description;
    private FilePart image;
}
