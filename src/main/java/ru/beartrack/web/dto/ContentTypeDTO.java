package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.beartrack.web.enums.ContentType;

@Data
@NoArgsConstructor
public class ContentTypeDTO {
    private ContentType contentType;
    private String information;

    public ContentTypeDTO(ContentType contentType) {
        this.contentType = contentType;
        this.information = contentType.getInformation();
    }
}
