package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.beartrack.web.dto.LocationDTO;
import ru.beartrack.web.utils.TransliterateUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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

    public Location(LocationDTO locationDTO, UUID userId) {
        try{
            setLatitude(Float.parseFloat(locationDTO.getLatitude()));
            setLongitude(Float.parseFloat(locationDTO.getLongitude()));
        }catch (Exception exception){
            log.error("Not valid latitude or Longitude! Error: [" + exception + "]");
        }
        setTitle(locationDTO.getTitle());
        setNotation(locationDTO.getNotation());
        setCreator(userId);
        setSef(TransliterateUtil.transliterate(locationDTO.getTitle()));
        setSubject(locationDTO.getSubject());
    }
}
