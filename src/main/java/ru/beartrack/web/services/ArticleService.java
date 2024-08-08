package ru.beartrack.web.services;

import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.ArticleDTO;
import ru.beartrack.web.models.*;
import ru.beartrack.web.repositories.ArticleImageRepository;
import ru.beartrack.web.repositories.ArticleRepository;
import ru.beartrack.web.utils.ImageioUtil;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository imageRepository;

    private final MinioService minioService;
    private final ManticoreService manticoreService;

    private final String tableName = "article";

    public ArticleService(ArticleRepository articleRepository, ArticleImageRepository imageRepository, MinioService minioService, ManticoreService manticoreService) {
        this.articleRepository = articleRepository;
        this.imageRepository = imageRepository;
        this.minioService = minioService;
        this.manticoreService = manticoreService;
        manticoreService.createIndex(tableName);
    }

    //#############################################################################################################################################################
    /*
    CREATE - START
     */
    @CacheEvict(value = "articles", allEntries = true)
    public Mono<Article> createArticle(ArticleDTO articleDTO, ApplicationUser user) {
        return Mono.just(new Article(articleDTO, user)).flatMap(article -> saveArticle(article,articleDTO)).flatMap(article -> {
           manticoreService.addDocument(tableName,getDocument(article));
           return Mono.just(article);
        });
    }
    /*
    CREATE - END
     */
    //#############################################################################################################################################################
    /*
    READ - START
     */
    @Cacheable("articles")
    public Mono<Article> getBySef(String sef) {
        return articleRepository.findBySef(sef).flatMap(this::setupImages);
    }

    @Cacheable("articles")
    public Flux<Article> getAllOrderByDate() {
        return articleRepository.findAllByOrderByCreatedDesc().flatMap(this::setupImages).collectList().flatMapMany(articles -> {
            articles = articles.stream().sorted(Comparator.comparing(Article::getCreated).reversed()).collect(Collectors.toList());
            return Flux.fromIterable(articles);
        }).flatMapSequential(Mono::just);
    }

    @Cacheable("articles")
    public Flux<Article> getAllOrderByDate(Pageable pageable) {
        return articleRepository.findAllByOrderByCreatedDesc(pageable).flatMapSequential(this::setupImages);
    }

    @Cacheable("articles")
    public Flux<Article> getAllByUserUuid(UUID uuid) {
        return articleRepository.findByCreator(uuid).flatMap(this::setupImages).collectList().flatMapMany(articles -> {
            articles = articles.stream().sorted(Comparator.comparing(Article::getCreated).reversed()).collect(Collectors.toList());
            return Flux.fromIterable(articles);
        }).flatMapSequential(Mono::just);
    }
    /*
    READ - END
     */
    //#############################################################################################################################################################
    /*
    UPDATE - START
     */
    @CacheEvict(value = "articles", allEntries = true)
    public Mono<Article> updateArticle(ArticleDTO articleDTO) {
        return imageRepository.findByParent(articleDTO.getUuid()).collectList().flatMap(images -> {
            for(ArticleImage articleImage : images){
                deleteFile(articleImage.getImageUrlSm());
                deleteFile(articleImage.getImageUrlMd());
                deleteFile(articleImage.getImageUrlLg());
            }
            return imageRepository.deleteAll(images);
        }).then(Mono.just(articleDTO).flatMap(dto -> {
            UUID uuid = dto.getUuid();
            return articleRepository.findByUuid(uuid).flatMap(article -> {
                article.setUpdated(LocalDate.now());
                article.update(articleDTO);
                return saveArticle(article,articleDTO).flatMap(updated -> {
                    manticoreService.updateDocument(tableName,getDocument(updated));
                    return Mono.just(updated);
                });
            });
        }));
    }
    /*
    UPDATE - END
     */
    //#############################################################################################################################################################
    /*
    DELETE - START
     */
    @CacheEvict(value = "articles", allEntries = true)
    public Mono<Article> deleteByUuid(UUID uuid) {
        return imageRepository.findByParent(uuid).collectList().flatMap(articleImages -> {
            for(ArticleImage articleImage : articleImages){
                deleteFile(articleImage.getImageUrlSm());
                deleteFile(articleImage.getImageUrlMd());
                deleteFile(articleImage.getImageUrlLg());
            }
            return imageRepository.deleteAll(articleImages);
        }).then(articleRepository.findByUuid(uuid).flatMap(article -> {
            manticoreService.deleteDocument(tableName,article.getUuid());
            return articleRepository.delete(article).then(Mono.just(article));
        }));
    }
    /*
    DELETE - END
     */
    //#############################################################################################################################################################

    private Mono<Article> setupImages(Article article){
        return imageRepository.findByParent(article.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(ArticleImage::getImageName)).collect(Collectors.toList());
            article.setImages(l);
            return Mono.just(article);
        });
    }

    private Mono<Article> saveArticle(Article article, ArticleDTO articleDTO){
        return articleRepository.save(article).flatMap(saved -> {
            if(articleDTO.getImages().size() != 0){
                return Flux.fromIterable(articleDTO.getImages()).flatMap(image -> {
                    try {
                        String fileName = String.valueOf(UUID.randomUUID());
                        return ImageioUtil.saveImage(image,fileName).flatMap(originalFile -> {
                            try {
                                ArticleImage articleImage = new ArticleImage();
                                articleImage.setParent(saved.getUuid());
                                articleImage.setImageName(image.filename());
                                String[] sizes = {"300", "640", "1280"};
                                ImageioUtil.createResizedImages(originalFile,fileName,sizes);
                                for (String size : sizes) {
                                    File resizedFile = new File(originalFile.getParent(), FilenameUtils.removeExtension(fileName) + "-" + size + ".webp");
                                    String objectName = "/articles/" + saved.getUuid() + "/" + resizedFile.getName();
                                    String imageUrl = minioService.uploadFile(resizedFile, objectName);
                                    //log.info("Uploaded image URL: " + imageUrl);
                                    if(size.equals(sizes[0])){
                                        articleImage.setImageUrlSm(imageUrl);
                                    }else if(size.equals(sizes[1])){
                                        articleImage.setImageUrlMd(imageUrl);
                                    }else{
                                        articleImage.setImageUrlLg(imageUrl);
                                    }
                                }
                                ImageioUtil.releaseTemp(fileName,sizes);
                                return imageRepository.save(articleImage);
                            } catch (IOException e) {
                                return Mono.error(new RuntimeException(e));
                            }
                        });
                    } catch (IOException e) {
                        return Flux.error(new RuntimeException(e));
                    }
                }).collectList().flatMap(articleImages -> Mono.just(saved));
            }else{
                return Mono.just(saved);
            }
        });
    }

    private void deleteFile(String imageUrl){
        String[] imageParts = imageUrl.split("/");
        String fileName = imageParts[imageParts.length - 3] + "/" + imageParts[imageParts.length - 2] + "/" + imageParts[imageParts.length - 1];
        minioService.deleteFile(fileName);
    }

    private Map<String,Object> getDocument(Article article){
        Map<String,Object> document = new HashMap<>();
        document.put("uuid",article.getUuid());
        document.put("title",article.getTitle());
        document.put("notation",article.getNotation());
        document.put("metaTitle",article.getMetaTitle());
        document.put("metaDescription",article.getMetaDescription());
        document.put("metaKeywords",article.getKeywords());
        document.put("content", article.getContent());
        return document;
    }

    public Mono<Long> getCount() {
        return articleRepository.count();
    }
}
