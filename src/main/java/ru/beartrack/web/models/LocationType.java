package ru.beartrack.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import ru.beartrack.web.dto.LocationTypeDTO;

import java.util.UUID;

@Data
@NoArgsConstructor
@Table(name = "location_type")
public class LocationType {
    @Id
    private UUID uuid;
    private String name;
    private String description;
    private String imageUrl;

    public LocationType(LocationTypeDTO locationTypeDTO) {
        setName(locationTypeDTO.getName());
        setDescription(locationTypeDTO.getDescription());
    }
}
