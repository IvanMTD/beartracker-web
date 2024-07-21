package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;
import ru.beartrack.web.dto.LocationDTO;
import ru.beartrack.web.utils.TransliterateUtil;

import java.time.LocalDate;
import java.util.*;

@Data
@Slf4j
@NoArgsConstructor
@Table(name = "location")
public class Location {
    @Id
    private UUID uuid;

    private String sef;
    private UUID creator;
    private float latitude;
    private float longitude;
    private String title;
    private String notation;
    private UUID subject;
    private String metaTitle;
    private String metaDescription;
    private long count;
    private UUID locationType;
    private Set<String> metaKeywords = new HashSet<>();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate created;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate updated;

    @Transient
    private List<LocationContent> contentList = new ArrayList<>();
    @Transient
    private Subject subjectModel;
    @Transient
    private LocationType locationTypeModel;


    public Location(LocationDTO locationDTO, UUID userId, long count) {
        try{
            setLatitude(Float.parseFloat(locationDTO.getLatitude()));
            setLongitude(Float.parseFloat(locationDTO.getLongitude()));
        }catch (Exception exception){
            log.error("Not valid latitude or Longitude! Error: [" + exception.getMessage() + "]");
        }
        setTitle(locationDTO.getTitle());
        setNotation(locationDTO.getNotation());
        setCreator(userId);
        setSef(TransliterateUtil.transliterate(locationDTO.getTitle()));
        setSubject(locationDTO.getSubject());
        setLocationType(locationDTO.getLocationType());

        setMetaTitle(locationDTO.getMetaTitle());
        setMetaDescription(locationDTO.getMetaDescription());
        String[] keywords = locationDTO.getMetaKeywords().split(", ");
        metaKeywords.addAll(Arrays.asList(keywords));
        setCreated(LocalDate.now());
        setCount(count);
    }

    public void update(LocationDTO locationDTO) {
        try{
            setLatitude(Float.parseFloat(locationDTO.getLatitude()));
            setLongitude(Float.parseFloat(locationDTO.getLongitude()));
        }catch (Exception exception){
            log.error("Not valid latitude or Longitude! Error: [" + exception.getMessage() + "]");
        }
        setTitle(locationDTO.getTitle());
        setNotation(locationDTO.getNotation());
        setSef(TransliterateUtil.transliterate(locationDTO.getTitle()));
        setSubject(locationDTO.getSubject());
        setLocationType(locationDTO.getLocationType());

        setMetaTitle(locationDTO.getMetaTitle());
        setMetaDescription(locationDTO.getMetaDescription());
        String[] keywords = locationDTO.getMetaKeywords().split(", ");
        metaKeywords.clear();
        metaKeywords.addAll(Arrays.asList(keywords));
        setUpdated(LocalDate.now());
    }

    public String getKeywords(){
        StringBuilder s = new StringBuilder();
        for(String keyword : metaKeywords){
            s.append(keyword).append(", ");
        }
        return s.substring(0,s.length()-2);
    }
}
