package com.grup14.luterano.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class HeaderAliases {
    private HeaderAliases() {}

    public static String canon(String header) {
        if (header == null) return "";


        String s = header.replace("\uFEFF", "").trim();
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT);
        s = s.replace(".", "");
        s = s.replaceAll("\\s+", " ");
        s = s.trim();
        switch (s) {
            case "grado/año":
            case "grado/ano":
            case "grado año":
            case "grado ano":
                return "grado/ano";

            case "division":
            case "división":
                return "division";

            case "plan de estu":
            case "plan de estudio":
            case "plan de est":
            case "plan de estu ":
                return "plan de estu";

            case "nro docum":
            case "nro documento":
            case "numero documento":
            case "nro. docum":
            case "nro. documento":
                return "nro docum";

            case "apellido":
                return "apellido";

            case "nombre":
                return "nombre";

            case "fecha nacimiento":
            case "fecha de nacimiento":
                return "fecha nacimiento";

            default:
                return s;
        }
    }
}
