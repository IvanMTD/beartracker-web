package ru.beartrack.web.enums;

public enum ContentType {
    BG_IMAGE ("Изображение занимает весь блок, контент отсутствует"),
    BG_IMAGE_CONTENT ("Изображение занимает весь блок, а контент отображается на его фоне"),
    IMAGE_LEFT_CONTENT ("Изображение отображается с лева, а контент с права от него"),
    IMAGE_RIGHT_CONTENT ("Изображение отображается с права, а контент с лева от него"),
    CONTENT ("Контент занимает весь блок, изображение отсутствует");

    private String information;

    ContentType(String information){
        this.information = information;
    }

    public String getInformation() {
        return information;
    }
}
