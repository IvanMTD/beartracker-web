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
import java.util.Comparator;
import java.util.UUID;
import java.util.stream.Collectors;

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
                locationContent.setContentType(ContentType.valueOf(block.getType()));
                locationContent.setContentTitle(block.getContentTitle());
                locationContent.setContent(block.getContent());
                locationContent.setPosition(Integer.parseInt(block.getPosition()));
                locationContent.setParent(preSaved.getUuid());
                locationContent.setImageDescription(block.getImageDescription());
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
                    }).switchIfEmpty(contentRepository.save(locationContent));
                } catch (Exception e) {
                    log.error("Error processing image", e);
                }
                return contentRepository.save(locationContent);
            }).collectList().flatMap(l -> locationRepository.save(preSaved));
        });
    }

    public Flux<Location> getAll() {
        return locationRepository.findAll().flatMap(location -> contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        }));
    }

    public Mono<Location> getBySef(String sef) {
        return locationRepository.findBySef(sef).flatMap(location -> contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        }));
    }

    public Flux<Location> getAllByUserUuid(UUID uuid) {
        return locationRepository.findAllByCreator(uuid).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Location::getCreated)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }
}
