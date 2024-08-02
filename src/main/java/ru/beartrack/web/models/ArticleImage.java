package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Data
@NoArgsConstructor
public class ArticleImage {
    @Id
    private UUID uuid;
    private UUID parent;

    private String imageUrlSm;
    private String imageUrlMd;
    private String imageUrlLg;
}
