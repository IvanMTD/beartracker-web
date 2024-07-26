package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.beartrack.web.models.Location;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class GlobalSearchDTO {
    private List<Location> locations = new ArrayList<>();
}
