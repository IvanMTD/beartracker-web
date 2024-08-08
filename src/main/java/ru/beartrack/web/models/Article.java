package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import ru.beartrack.web.dto.ArticleDTO;
import ru.beartrack.web.utils.TransliterateUtil;

import java.time.LocalDate;
import java.util.*;

@Data
@NoArgsConstructor
@Table(name = "article")
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

    @Transient
    private List<ArticleImage> images = new ArrayList<>();

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
        for(String link : articleDTO.getLinks()){
            String[] linkParts = link.split("/");
            String locationSef = linkParts[linkParts.length - 1];
            links.add(locationSef);
        }
    }

    public String getKeywords(){
        StringBuilder s = new StringBuilder();
        for(String keyword : metaKeywords){
            s.append(keyword).append(", ");
        }
        return s.substring(0,s.length()-2);
    }

    public void update(ArticleDTO articleDTO) {
        setSef(TransliterateUtil.transliterate(articleDTO.getTitle()));
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
        for(String link : articleDTO.getLinks()){
            String[] linkParts = link.split("/");
            String locationSef = linkParts[linkParts.length - 1];
            links.add(locationSef);
        }
    }
}
