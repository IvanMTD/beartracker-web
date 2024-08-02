package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import ru.beartrack.web.dto.ArticleDTO;
import ru.beartrack.web.utils.TransliterateUtil;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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

    private Set<String> links = new HashSet<>();

    private String metaTitle;
    private String metaDescription;
    private Set<String> metaKeywords = new HashSet<>();

    private LocalDate created;
    private LocalDate updated;

    public Article(ArticleDTO articleDTO, ApplicationUser user) {
        setSef(TransliterateUtil.transliterate(articleDTO.getTitle()));
        setCreator(user.getUuid());
        setTitle(articleDTO.getTitle());
        setNotation(articleDTO.getNotation());
        setContent(articleDTO.getContent());
        setMetaTitle(articleDTO.getMetaTitle());
        setMetaDescription(articleDTO.getMetaDescription());
        metaKeywords.clear();
        String[] keywords = articleDTO.getMetaKeywords().split(",");
        for(String keyword : keywords){
            keyword = keyword.trim();
            metaKeywords.add(keyword);
        }
        setCreated(LocalDate.now());
        metaKeywords = new HashSet<>();
        for(String link : articleDTO.getLinks()){
            String[] linkParts = link.split("/");
            String locationSef = linkParts[linkParts.length - 1];
            metaKeywords.add(locationSef);
        }
    }
}
