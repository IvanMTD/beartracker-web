package ru.beartrack.web.enums;

import reactor.core.publisher.Flux;
import ru.beartrack.web.dto.ContentTypeDTO;
import ru.beartrack.web.dto.RoleDTO;

import java.util.ArrayList;
import java.util.List;

public enum Role {
    USER("Пользователь"),MODERATOR("Модератор"),ADMIN("Администратор");
    private String title;

    Role(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static Flux<RoleDTO> valuesOfDTO(){
        List<RoleDTO> list = new ArrayList<>();
        for(Role role : Role.values()){
            list.add(new RoleDTO(role));
        }
        return Flux.fromIterable(list);
    }
}
