package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ArticleDTO {
    private UUID uuid;
    private String title;
    private String notation;
    private String content;
    private List<FilePart> images = new ArrayList<>();
    private List<String> links = new ArrayList<>();
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
}
