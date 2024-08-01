package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ArticleDTO {
    private String title;
    private String notation;
    private String content;
    private List<String> images = new ArrayList<>();
    private List<String> links = new ArrayList<>();
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
}
