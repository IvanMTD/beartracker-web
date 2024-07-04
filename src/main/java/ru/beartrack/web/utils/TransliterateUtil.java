package ru.beartrack.web.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class TransliterateUtil {

    private static final Map<Character, String> TRANSLIT_MAP = new HashMap<>();

    static {
        TRANSLIT_MAP.put('а', "a");
        TRANSLIT_MAP.put('б', "b");
        TRANSLIT_MAP.put('в', "v");
        TRANSLIT_MAP.put('г', "g");
        TRANSLIT_MAP.put('д', "d");
        TRANSLIT_MAP.put('е', "e");
        TRANSLIT_MAP.put('ё', "yo");
        TRANSLIT_MAP.put('ж', "zh");
        TRANSLIT_MAP.put('з', "z");
        TRANSLIT_MAP.put('и', "i");
        TRANSLIT_MAP.put('й', "y");
        TRANSLIT_MAP.put('к', "k");
        TRANSLIT_MAP.put('л', "l");
        TRANSLIT_MAP.put('м', "m");
        TRANSLIT_MAP.put('н', "n");
        TRANSLIT_MAP.put('о', "o");
        TRANSLIT_MAP.put('п', "p");
        TRANSLIT_MAP.put('р', "r");
        TRANSLIT_MAP.put('с', "s");
        TRANSLIT_MAP.put('т', "t");
        TRANSLIT_MAP.put('у', "u");
        TRANSLIT_MAP.put('ф', "f");
        TRANSLIT_MAP.put('х', "kh");
        TRANSLIT_MAP.put('ц', "ts");
        TRANSLIT_MAP.put('ч', "ch");
        TRANSLIT_MAP.put('ш', "sh");
        TRANSLIT_MAP.put('щ', "she");
        TRANSLIT_MAP.put('ъ',"");
        TRANSLIT_MAP.put('ы', "ui");
        TRANSLIT_MAP.put('ь',"");
        TRANSLIT_MAP.put('э', "ie");
        TRANSLIT_MAP.put('ю', "yu");
        TRANSLIT_MAP.put('я', "ya");
        TRANSLIT_MAP.put(' ', "_");
    }

    public static String transliterate(String stringData) {
        StringBuilder result = new StringBuilder();
        for (char character : stringData.toLowerCase().toCharArray()) {
            String transliteratedChar = TRANSLIT_MAP.get(character);
            if (transliteratedChar != null) {
                result.append(transliteratedChar);
            }
        }
        return result.toString();
    }
}
