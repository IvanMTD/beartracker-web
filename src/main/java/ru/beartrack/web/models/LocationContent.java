package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
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
}
