package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.ArticleDTO;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.models.Article;
import ru.beartrack.web.models.ArticleImage;
import ru.beartrack.web.repositories.ArticleImageRepository;
import ru.beartrack.web.repositories.ArticleRepository;
import ru.beartrack.web.utils.ImageioUtil;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository imageRepository;

    private final MinioService minioService;

    @Cacheable("article")
    public Mono<Article> getBySef(String sef) {
        return articleRepository.findBySef(sef).flatMap(this::setupImages);
    }

    @CacheEvict("articles")
    public Mono<Article> createArticle(ArticleDTO articleDTO, ApplicationUser user) {
        return Mono.just(new Article(articleDTO, user)).flatMap(article -> {
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
        });
    }

    @Cacheable("articles")
    public Flux<Article> getAllOrderByDate() {
        return articleRepository.findAllByOrderByCreatedDesc().flatMap(this::setupImages).collectList().flatMapMany(articles -> {
            articles = articles.stream().sorted(Comparator.comparing(Article::getCreated).reversed()).collect(Collectors.toList());
            return Flux.fromIterable(articles);
        }).flatMapSequential(Mono::just);
    }

    @Cacheable("articles")
    public Flux<Article> getAllByUserUuid(UUID uuid) {
        return articleRepository.findByCreator(uuid).flatMap(this::setupImages).collectList().flatMapMany(articles -> {
            articles = articles.stream().sorted(Comparator.comparing(Article::getCreated).reversed()).collect(Collectors.toList());
            return Flux.fromIterable(articles);
        }).flatMapSequential(Mono::just);
    }

    private Mono<Article> setupImages(Article article){
        return imageRepository.findByParent(article.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(ArticleImage::getImageName)).collect(Collectors.toList());
            article.setImages(l);
            return Mono.just(article);
        });
    }
}
