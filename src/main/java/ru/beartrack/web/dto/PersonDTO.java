package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class PersonDTO {
    private UUID uuid;
    private String username;
    private String password;
    private String lastname;
    private String name;
    private String middleName;
    private String birthday;
    private String placedAt;
    private String email;
    private String role;
}
