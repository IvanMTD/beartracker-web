package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
public class Article {
    @Id
    private UUID uuid;

    private String sef;
    private UUID creator;
    private String title;
    private String notation;
    private String content;

    private String metaTitle;
    private String metaDescription;
    private Set<String> metaKeywords = new HashSet<>();

    private LocalDate created;
    private LocalDate updated;
}
