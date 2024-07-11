package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.beartrack.web.dto.ContentDTO;
import ru.beartrack.web.enums.ContentType;

import java.util.UUID;

@Data
@Slf4j
@NoArgsConstructor
@Table(name = "location_content")
public class LocationContent {
    @Id
    private UUID uuid;

    private UUID parent;
    private int position;
    private ContentType contentType;
    private String contentTitle;
    private String content;
    private String imageUrlSm;
    private String imageUrlMd;
    private String imageUrlLg;
    private String imageDescription;

    public LocationContent(ContentDTO block, UUID parent) {
        setParent(parent);
        setPosition(Integer.parseInt(block.getPosition()));
        setContentType(ContentType.valueOf(block.getType()));
        setContentTitle(block.getContentTitle());
        setContent(block.getContent());
        setImageDescription(block.getImageDescription());
    }

    public void baseUpdate(ContentDTO block) {
        setPosition(Integer.parseInt(block.getPosition()));
        setContentType(ContentType.valueOf(block.getType()));
        setContentTitle(block.getContentTitle());
        setContent(block.getContent());
        setImageDescription(block.getImageDescription());
    }
}
