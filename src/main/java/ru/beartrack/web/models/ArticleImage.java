package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "article_image")
public class ArticleImage {
    @Id
    private UUID uuid;
    private UUID parent;

    private String imageUrlSm;
    private String imageUrlMd;
    private String imageUrlLg;

    private String imageName;
}
