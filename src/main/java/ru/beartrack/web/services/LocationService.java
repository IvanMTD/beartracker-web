package ru.beartrack.web.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.beartrack.web.dto.ContentDTO;
import ru.beartrack.web.dto.LocationDTO;
import ru.beartrack.web.dto.LocationTypeDTO;
import ru.beartrack.web.enums.ContentType;
import ru.beartrack.web.models.ApplicationUser;
import ru.beartrack.web.models.Location;
import ru.beartrack.web.models.LocationContent;
import ru.beartrack.web.models.LocationType;
import ru.beartrack.web.repositories.LocationContentRepository;
import ru.beartrack.web.repositories.LocationRepository;
import ru.beartrack.web.repositories.LocationTypeRepository;
import ru.beartrack.web.repositories.SubjectRepository;
import ru.beartrack.web.utils.ImageioUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationContentRepository contentRepository;
    private final LocationTypeRepository typeRepository;
    private final SubjectRepository subjectRepository;
    private final MinioService minioService;

    public Mono<Location> saveLocation(LocationDTO locationDTO, UUID userID){
        return locationRepository.count().flatMap( count -> Mono.just(new Location(locationDTO, userID, (count + 1))).flatMap(locationRepository::save).flatMap(preSaved -> Flux.fromIterable(locationDTO.getBlocks()).flatMap(block -> {
            LocationContent locationContent = new LocationContent();
            locationContent.setContentType(ContentType.valueOf(block.getType()));
            locationContent.setContentTitle(block.getContentTitle());
            locationContent.setContent(block.getContent());
            locationContent.setPosition(Integer.parseInt(block.getPosition()));
            locationContent.setParent(preSaved.getUuid());
            locationContent.setImageDescription(block.getImageDescription());
            return saveImage(block,locationContent,preSaved).flatMap(lc -> contentRepository.save(locationContent));
        }).collectList().flatMap(l -> locationRepository.save(preSaved))));
    }

    public Flux<Location> getAll() {
        return locationRepository.findAll().flatMap(location -> contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        }));
    }

    public Flux<Location> getAllOrderByCreated() {
        return locationRepository.findAllByOrderByCreatedDesc().flatMap(location -> contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        }));
    }

    public Flux<Location> getAllOrderByCount() {
        return locationRepository.findAllByOrderByCountDesc().flatMapSequential(location -> {
            if(location.getLocationType() != null){
                return typeRepository.findByUuid(location.getLocationType()).flatMap(type -> {
                    location.setLocationTypeModel(type);
                    return contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
                        l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
                        location.setContentList(l);
                        return Mono.just(location);
                    });
                });
            }else{
                return contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
                    l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
                    location.setContentList(l);
                    return Mono.just(location);
                });
            }
        });
        /*return locationRepository.findAllByOrderByCountDesc().flatMapSequential(location -> contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        }));*/
    }

    public Mono<Location> getBySef(String sef) {
        return locationRepository.findBySef(sef).flatMap(location -> typeRepository.findByUuid(location.getLocationType()).flatMap(type -> {
            location.setLocationTypeModel(type);
            return setupContent(location);
        }).switchIfEmpty(Mono.just(location).flatMap(this::setupContent)));
    }

    public Flux<Location> getAllByUserUuid(UUID uuid) {
        return locationRepository.findAllByCreator(uuid).collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(Location::getCount).reversed()).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    public Mono<Boolean> update(LocationDTO locationPost) {
        return locationRepository.findByUuid(locationPost.getUuid()).flatMap(location -> {
            location.update(locationPost);
            return locationRepository.save(location).flatMapMany(locationUpdated -> {
                log.info("location has been updated [{}]",locationUpdated);
                return contentRepository.findByParent(locationUpdated.getUuid());
            }).collectList();
        }).flatMap(contents -> {
            contents = contents.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            List<ContentDTO> blocks = locationPost.getBlocks();
            List<ContentDTO> filtered = new ArrayList<>();
            for(ContentDTO block : blocks){
                if(block.getType() != null){
                    filtered.add(block);
                }
            }
            blocks = new ArrayList<>(filtered);

            Set<LocationContent> onDelete = new HashSet<>();
            for(LocationContent content : contents){
                boolean exist = false;
                for(ContentDTO block : blocks){
                    if(block.getUuid() != null) {
                        if (content.getUuid().equals(block.getUuid())) {
                            exist = true;
                            break;
                        }
                    }
                }
                if(!exist){
                    onDelete.add(content);
                }
            }

            deleteFiles(onDelete);

            return contentRepository.deleteAll(onDelete).then(Flux.fromIterable(blocks).flatMap(block -> {
                return contentRepository.findByUuid(block.getUuid()).flatMap(locationContent -> {
                    locationContent.baseUpdate(block);
                    if(block.getImage() != null){
                        return locationRepository.findByUuid(locationPost.getUuid()).flatMap(location -> saveImage(block,locationContent,location).flatMap(contentRepository::save));
                    }else{
                        return contentRepository.save(locationContent);
                    }
                }).switchIfEmpty(Mono.just(new LocationContent(block, locationPost.getUuid())).flatMap(lc -> {
                    if(block.getImage() != null){
                        return locationRepository.findByUuid(locationPost.getUuid()).flatMap(location -> saveImage(block,lc,location).flatMap(contentRepository::save));
                    }else{
                        return contentRepository.save(lc);
                    }
                }));
            }).collectList().flatMap(l -> {
                log.info("content list updated [{}]",l);
                return Mono.just(true);
            }));
        });
    }

    public Mono<Location> delete(UUID uuid) {
        return locationRepository.findByUuid(uuid).flatMap(location -> {
            log.info("found location {}", location.getUuid());
            return contentRepository.findByParent(location.getUuid()).flatMap(content -> {
                return contentRepository.delete(content).then(Mono.just(content));
            }).collectList().flatMap(l -> {
                location.setContentList(l);
                Set<LocationContent> sl = new HashSet<>(l);
                deleteFiles(sl);
                return locationRepository.delete(location).then(Mono.just(location));
            });
        });
    }

    private Mono<LocationContent> saveImage(ContentDTO block, LocationContent locationContent, Location preSaved){
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
        return Mono.just(locationContent);
    }

    private void deleteFiles(Set<LocationContent> onDelete){
        for(LocationContent locationContent : onDelete){
            if(locationContent.getImageUrlSm() != null) {
                String[] smParts = locationContent.getImageUrlSm().split("/");
                String smName = smParts[smParts.length - 3] + "/" + smParts[smParts.length - 2] + "/" + smParts[smParts.length - 1];
                String[] mdParts = locationContent.getImageUrlMd().split("/");
                String mdName = mdParts[mdParts.length - 3] + "/" + mdParts[mdParts.length - 2] + "/" + mdParts[mdParts.length - 1];
                String[] lgParts = locationContent.getImageUrlLg().split("/");
                String lfName = lgParts[lgParts.length - 3] + "/" + lgParts[lgParts.length - 2] + "/" + lgParts[lgParts.length - 1];
                List<String> names = Arrays.asList(smName, mdName, lfName);
                for (String fileName : names) {
                    minioService.deleteFile(fileName);
                }
            }
        }
    }

    public Mono<Long> getCount() {
        return locationRepository.count();
    }

    public Flux<Location> getAllOrderByCreated(Pageable pageable) {
        return locationRepository.findAllByOrderByCreatedDesc(pageable).flatMap(location -> contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        }));
    }

    public Flux<Location> getAllOrderByCount(Pageable pageable) {
        return locationRepository.findAllByOrderByCountDesc(pageable).flatMapSequential(location -> contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        }));
    }

    public Mono<Location> synchronise(Location location, ApplicationUser user) {
        return Mono.just(location).flatMap(loc -> {
            loc.setUuid(null);
            loc.setCreator(user.getUuid());
            loc.setCreated(LocalDate.now());
            loc.setUpdated(null);
            return subjectRepository.findByIso("RU-MOW").flatMap(subject -> {
                loc.setSubject(subject.getUuid());
                return locationRepository.save(loc);
            });
        }).flatMap(saved -> {
            log.info("location saved {}", saved);
            return Flux.fromIterable(location.getContentList()).flatMap(lc -> {
                lc.setUuid(null);
                lc.setParent(saved.getUuid());
                return contentRepository.save(lc);
            }).flatMap(lcSaved -> {
                log.info("saved content {}", lcSaved);
                return Mono.just(lcSaved);
            }).collectList().flatMap(l -> {
                return Mono.just(saved);
            });
        });
    }

    public Flux<LocationType> getAllLocationTypes() {
        return typeRepository.findAll().collectList().flatMapMany(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationType::getName)).collect(Collectors.toList());
            return Flux.fromIterable(l);
        }).flatMapSequential(Mono::just);
    }

    public Mono<LocationType> saveLocationType(LocationTypeDTO locationTypeDTO) {
        return Mono.just(new LocationType(locationTypeDTO)).flatMap(locationType -> {
            try {
                return saveImageSimple(locationTypeDTO.getImage()).flatMap(imageUrl -> {
                    locationType.setImageUrl(imageUrl);
                    return typeRepository.save(locationType);
                });
            } catch (IOException e) {
                return Mono.error(new RuntimeException(e));
            }
        });
    }

    private Mono<String> saveImageSimple(FilePart image) throws IOException {
        String extension = FilenameUtils.getExtension(image.filename());
        String fileName = UUID.randomUUID() + "." + extension;
        return ImageioUtil.saveImage(image,fileName).flatMap(originalFile -> {
            String objectName = "/images/location-types/" + fileName;
            String imageUrl = null;
            try {
                imageUrl = minioService.uploadFile(originalFile, objectName);
                ImageioUtil.releaseTemp(fileName,null);
            } catch (IOException e) {
                return Mono.error(new RuntimeException(e));
            }
            return Mono.just(imageUrl);
        });
    }

    private Mono<Location> setupContent(Location location){
        return contentRepository.findByParent(location.getUuid()).collectList().flatMap(l -> {
            l = l.stream().sorted(Comparator.comparing(LocationContent::getPosition)).collect(Collectors.toList());
            location.setContentList(l);
            return Mono.just(location);
        });
    }
}
