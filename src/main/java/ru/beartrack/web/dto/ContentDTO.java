package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ContentDTO {
    private UUID uuid;
    private String position;
    private String type;
    private String contentTitle;
    private String content;
    private FilePart image;
    private String imageDescription;
}
