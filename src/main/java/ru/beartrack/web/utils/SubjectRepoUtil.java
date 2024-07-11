package ru.beartrack.web.utils;

import ru.beartrack.web.dto.SubjectRecord;

import java.util.Arrays;
import java.util.List;

public class SubjectRepoUtil {
    private static SubjectRepoUtil instance;
    private final List<SubjectRecord> subjectRecords;

    protected SubjectRepoUtil(){
        subjectRecords = Arrays.asList(
                new SubjectRecord("Ивановская область", "RU-IVA", "CFO"),
                new SubjectRecord("Нижегородская область", "RU-NIZ", "PFO"),
                new SubjectRecord("Красноярский край", "RU-KYA", "SFO"),
                new SubjectRecord("Республика Башкортостан", "RU-BA", "PFO"),
                new SubjectRecord("Ханты-Мансийский автономный округ - Югра", "RU-KHM", "UFO"),
                new SubjectRecord("Чукотский автономный округ", "RU-CHU", "DFO"),
                new SubjectRecord("Республика Коми", "RU-KO", "SZFO"),
                new SubjectRecord("Брянская область", "RU-BRY", "CFO"),
                new SubjectRecord("Челябинская область", "RU-CHE", "UFO"),
                new SubjectRecord("Орловская область", "RU-ORL", "CFO"),
                new SubjectRecord("Пензенская область", "RU-PNZ", "PFO"),
                new SubjectRecord("Краснодарский край", "RU-KDA", "YFO"),
                new SubjectRecord("Тульская область", "RU-TUL", "CFO"),
                new SubjectRecord("Курская область", "RU-KRS", "CFO"),
                new SubjectRecord("Псковская область", "RU-PSK", "SZFO"),
                new SubjectRecord("Ростовская область", "RU-ROS", "YFO"),
                new SubjectRecord("Республика Калмыкия", "RU-KL", "YFO"),
                new SubjectRecord("Мурманская область", "RU-MUR", "SZFO"),
                new SubjectRecord("Калининградская область", "RU-KGD", "SZFO"),
                new SubjectRecord("Вологодская область", "RU-VLG", "SZFO"),
                new SubjectRecord("Еврейская автономная область", "RU-YEV", "DFO"),
                new SubjectRecord("Магаданская область", "RU-MAG", "DFO"),
                new SubjectRecord("Ненецкий автономный округ", "RU-NEN", "SZFO"),
                new SubjectRecord("Белгородская область", "RU-BEL", "CFO"),
                new SubjectRecord("Кабардино-Балкарская Республика", "RU-KB", "SKFO"),
                new SubjectRecord("Республика Ингушетия", "RU-IN", "SKFO"),
                new SubjectRecord("Республика Карелия", "RU-KR", "SZFO"),
                new SubjectRecord("Карачаево-Черкесская Республика", "RU-KC", "SKFO"),
                new SubjectRecord("Костромская область", "RU-KOS", "CFO"),
                new SubjectRecord("Республика Алтай", "RU-AL", "SFO"),
                new SubjectRecord("Смоленская область", "RU-SMO", "CFO"),
                new SubjectRecord("Республика Адыгея", "RU-AD", "YFO"),
                new SubjectRecord("Чувашская Республика", "RU-CU", "PFO"),
                new SubjectRecord("Тамбовская область", "RU-TAM", "CFO"),
                new SubjectRecord("Томская область", "RU-TOM", "SFO"),
                new SubjectRecord("Хабаровский край", "RU-KHA", "DFO"),
                new SubjectRecord("Ставропольский край", "RU-STA", "SKFO"),
                new SubjectRecord("Ульяновская область", "RU-ULY", "PFO"),
                new SubjectRecord("Республика Марий Эл", "RU-ME", "PFO"),
                new SubjectRecord("Омская область", "RU-OMS", "SFO"),
                new SubjectRecord("Забайкальский край", "RU-ZAB", "DFO"),
                new SubjectRecord("Республика Саха (Якутия)", "RU-SA", "DFO"),
                new SubjectRecord("Кировская область", "RU-KIR", "PFO"),
                new SubjectRecord("Новгородская область", "RU-NGR", "SZFO"),
                new SubjectRecord("Курганская область", "RU-KGN", "UFO"),
                new SubjectRecord("Оренбургская область", "RU-ORE", "PFO"),
                new SubjectRecord("Тюменская область", "RU-TYU", "UFO"),
                new SubjectRecord("Луганская Народная Республика", "UA-09", "YFO"),
                new SubjectRecord("Владимирская область", "RU-VLA", "CFO"),
                new SubjectRecord("Иркутская область", "RU-IRK", "SFO"),
                new SubjectRecord("Донецкая народная республика", "UA-14", "YFO"),
                new SubjectRecord("Архангельская область", "RU-ARK", "SZFO"),
                new SubjectRecord("Волгоградская область", "RU-VGG", "YFO"),
                new SubjectRecord("Ямало-Ненецкий автономный округ", "RU-YAN", "UFO"),
                new SubjectRecord("Республика Хакасия", "RU-KK", "SFO"),
                new SubjectRecord("Республика Дагестан", "RU-DA", "SKFO"),
                new SubjectRecord("Саратовская область", "RU-SAR", "PFO"),
                new SubjectRecord("Республика Крым", "UA-43", "YFO"),
                new SubjectRecord("Республика Северная Осетия - Алания", "RU-SE", "SKFO"),
                new SubjectRecord("Астраханская область", "RU-AST", "YFO"),
                new SubjectRecord("Приморский край", "RU-PRI", "DFO"),
                new SubjectRecord("Севастополь", "UA-40", "YFO"),
                new SubjectRecord("Чеченская Республика", "RU-CE", "SKFO"),
                new SubjectRecord("Кемеровская область", "RU-KEM", "SFO"),
                new SubjectRecord("Сахалинская область", "RU-SAK", "DFO"),
                new SubjectRecord("Ярославская область", "RU-YAR", "CFO"),
                new SubjectRecord("Камчатский край", "RU-KAM", "DFO"),
                new SubjectRecord("Республика Тыва", "RU-TY", "SFO"),
                new SubjectRecord("Республика Бурятия", "RU-BU", "DFO"),
                new SubjectRecord("Калужская область", "RU-KLU", "CFO"),
                new SubjectRecord("Херсонская область", "UA-65", "YFO"),
                new SubjectRecord("Амурская область", "RU-AMU", "DFO"),
                new SubjectRecord("Московская область", "RU-MOS", "CFO"),
                new SubjectRecord("Самарская область", "RU-SAM", "PFO"),
                new SubjectRecord("Москва", "RU-MOW", "CFO"),
                new SubjectRecord("Тверская область", "RU-TVE", "CFO"),
                new SubjectRecord("Республика Татарстан", "RU-TA", "PFO"),
                new SubjectRecord("Ленинградская область", "RU-LEN", "SZFO"),
                new SubjectRecord("Республика Мордовия", "RU-MO", "PFO"),
                new SubjectRecord("Алтайский край", "RU-ALT", "SFO"),
                new SubjectRecord("Новосибирская область", "RU-NVS", "SFO"),
                new SubjectRecord("Пермский край", "RU-PER", "PFO"),
                new SubjectRecord("Липецкая область", "RU-LIP", "CFO"),
                new SubjectRecord("Удмуртская Республика", "RU-UD", "PFO"),
                new SubjectRecord("Запорожская область", "UA-23", "YFO"),
                new SubjectRecord("Воронежская область", "RU-VOR", "CFO"),
                new SubjectRecord("Рязанская область", "RU-RYA", "CFO"),
                new SubjectRecord("Санкт-Петербург", "RU-SPE", "SZFO"),
                new SubjectRecord("Свердловская область", "RU-SVE", "UFO")
        );
    }

    public static SubjectRepoUtil getInstance() {
        if(instance == null){
            instance = new SubjectRepoUtil();
        }
        return instance;
    }

    public List<SubjectRecord> getSubjectRecords() {
        return subjectRecords;
    }
}
