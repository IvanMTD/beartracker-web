package ru.beartrack.web.utils;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class Transliterator {
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String transliterate(String stringData) {
        String normalized = Normalizer.normalize(stringData, Normalizer.Form.NFD);
        String latin = NONLATIN.matcher(normalized).replaceAll("");
        String lowerCase = latin.toLowerCase(Locale.getDefault());
        String underscored = WHITESPACE.matcher(lowerCase).replaceAll("_");
        return underscored;
    }
}
