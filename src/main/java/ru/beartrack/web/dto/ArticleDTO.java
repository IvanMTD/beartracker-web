package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ArticleDTO {
    private String title;
    private String notation;
    private String content;
    private List<FilePart> images = new ArrayList<>();
    private List<String> links = new ArrayList<>();
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
}
