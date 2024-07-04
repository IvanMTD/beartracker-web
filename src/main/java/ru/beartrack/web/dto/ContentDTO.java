package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

@Data
@NoArgsConstructor
public class ContentDTO {
    private String type;
    private String content;
    private FilePart image;
}
