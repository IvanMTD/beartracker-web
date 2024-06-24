package ru.beartrack.web.enums;

public enum Role {
    USER("Пользователь"),MODERATOR("Модератор"),ADMIN("Администратор");
    private String title;

    Role(String title){
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
