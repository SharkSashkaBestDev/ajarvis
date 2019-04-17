package com.asoft.ajarvis.actions.services;

import java.util.HashMap;
import java.util.Map;

public class Translit {
    private static final Map<String, String> letters = new HashMap<>();

    static {
        letters.put("а", "a");
        letters.put("б", "b");
        letters.put("в", "v");
        letters.put("г", "g");
        letters.put("д", "d");
        letters.put("е", "e");
        letters.put("ё", "e");
        letters.put("ж", "zh");
        letters.put("з", "z");
        letters.put("и", "i");
        letters.put("й", "i");
        letters.put("к", "k");
        letters.put("л", "l");
        letters.put("м", "m");
        letters.put("н", "n");
        letters.put("о", "o");
        letters.put("п", "p");
        letters.put("р", "r");
        letters.put("с", "s");
        letters.put("т", "t");
        letters.put("у", "u");
        letters.put("ф", "f");
        letters.put("х", "h");
        letters.put("ц", "c");
        letters.put("ч", "ch");
        letters.put("ш", "sh");
        letters.put("щ", "sch");
        letters.put("ы", "y");
        letters.put("э", "e");
        letters.put("ю", "yu");
        letters.put("я", "ya");
        letters.put(" ", "_");
    }

    public static String translit(String str) {
        StringBuilder resultStr = new StringBuilder(str.length());

        for (int i = 0; i<str.length(); i++) {
            String l = str.substring(i, i+1);
            resultStr.append(letters.getOrDefault(l, l));
        }

        return resultStr.toString();
    }
}
