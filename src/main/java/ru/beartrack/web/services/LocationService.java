package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.LocationDTO;
import ru.beartrack.web.enums.ContentType;
import ru.beartrack.web.models.Location;
import ru.beartrack.web.models.LocationContent;
import ru.beartrack.web.repositories.LocationContentRepository;
import ru.beartrack.web.repositories.LocationRepository;
import ru.beartrack.web.utils.ImageioUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationContentRepository contentRepository;
    private final MinioService minioService;

    public Mono<Location> saveLocation(LocationDTO locationDTO, UUID userID){
        return Mono.just(new Location(locationDTO, userID)).flatMap(locationRepository::save).flatMap(preSaved -> {
            return Flux.fromIterable(locationDTO.getBlocks()).flatMap(block -> {
                LocationContent locationContent = new LocationContent();
                ContentType type = ContentType.valueOf(block.getType());
                String content = block.getContent();
                locationContent.setContentType(type);
                locationContent.setContent(content);
                try {
                    FilePart filePart = block.getImage();
                    String fileName = UUID.randomUUID() + ".webp";
                    return ImageioUtil.saveImage(filePart,fileName).flatMap(originalFile -> {
                        try {
                            String[] sizes = {"300", "640", "1280"};
                            ImageioUtil.createResizedImages(originalFile,fileName,sizes);
                            for (String size : sizes) {
                                File resizedFile = new File(originalFile.getParent(), FilenameUtils.removeExtension(fileName) + "-" + size + ".webp");
                                String objectName = "/images/" + preSaved.getUuid() + "/" + resizedFile.getName();
                                String imageUrl = minioService.uploadFile(resizedFile, objectName);
                                log.info("Uploaded image URL: " + imageUrl);
                                if(size.equals(sizes[0])){
                                    locationContent.setImageUrlSm(imageUrl);
                                }else if(size.equals(sizes[1])){
                                    locationContent.setImageUrlMd(imageUrl);
                                }else{
                                    locationContent.setImageUrlLg(imageUrl);
                                }
                            }

                            ImageioUtil.releaseTemp(fileName,sizes);

                            return contentRepository.save(locationContent);
                        } catch (IOException e) {
                            return Mono.empty();
                        }
                    });
                } catch (Exception e) {
                    log.error("Error processing image", e);
                }
                return contentRepository.save(locationContent);
            }).collectList().flatMap(l -> {
                Set<UUID> uuids = new HashSet<>();
                for(LocationContent lc : l){
                    uuids.add(lc.getUuid());
                }
                preSaved.setLocationContentList(uuids);
                return locationRepository.save(preSaved);
            });
        });
    }
}
