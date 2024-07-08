package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import ru.beartrack.web.models.LocationContent;

@Data
@NoArgsConstructor
public class ContentDTO {
    private String position;
    private String type;
    private String contentTitle;
    private String content;
    private FilePart image;
    private String imageDescription;
}
