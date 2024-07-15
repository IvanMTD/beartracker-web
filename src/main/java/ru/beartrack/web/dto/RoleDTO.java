package ru.beartrack.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.beartrack.web.enums.Role;

@Data
@NoArgsConstructor
public class RoleDTO {
    private String roleTitle;
    private Role role;

    public RoleDTO(Role role){
        this.roleTitle = role.getTitle();
        this.role = role;
    }
}
