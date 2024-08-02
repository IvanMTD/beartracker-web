package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleImageRepository imageRepository;

    private final MinioService minioService;

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
}
