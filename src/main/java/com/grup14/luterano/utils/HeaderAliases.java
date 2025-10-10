package com.grup14.luterano.utils;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HeaderAliases {
    private HeaderAliases() {}

    // notaXetapaY (ej: nota1etapa2)
    private static final Pattern NOTA_ETAPA = Pattern.compile(".*?nota\\s*(\\d+)\\s*etapa\\s*(\\d+).*");

    public static String canon(String header) {
        if (header == null) return "";

        // Quitar BOM, normalizar, minúsculas
        String s = header.replace("\uFEFF", "").trim();
        s = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}+", ""); // sin tildes
        s = s.toLowerCase(Locale.ROOT);

        // Reemplazar símbolos comunes por espacio
        // (n°/º, /, ., -, _ , ;) -> espacios y colapsar
        s = s.replace('º', ' ');
        s = s.replace('°', ' ');
        s = s.replace('/', ' ');
        s = s.replace('.', ' ');
        s = s.replace('-', ' ');
        s = s.replace('_', ' ');
        s = s.replace(';', ' ');
        s = s.replaceAll("\\s+", " ").trim();

        // Normalizar variantes típicas
        switch (s) {
            case "grado año":
            case "grado ano":
            case "grado/año":
            case "grado/ano":
                return "gradoano";

            case "division":
            case "división":
                return "division";

            case "turno":
                return "turno";

            case "plan de estu":
            case "plan de estu ":
            case "plan de est":
            case "plan de estudio":
            case "plan de estudios":
                return "plandestu";

            case "ciclo":
            case "ciclo basico":
            case "ciclo básico":
                return "ciclo";

            case "nro docum":
            case "nro documento":
            case "nro. docum":
            case "nro. documento":
            case "numero documento":
            case "n° documento":
            case "n documento":
            case "dni":
                return "nrodocumento";

            case "apellido":
                return "apellido";

            case "nombre":
                return "nombre";

            case "fecha nacimiento":
            case "fecha de nacimiento":
            case "fec nacimiento":
                return "fechanacimiento";

            case "espacio curricular":
                return "espaciocurricular";

            case "codigo espacio curricular":
            case "código espacio curricular":
            case "cod espacio curricular":
            case "cod. espacio curricular":
                return "codigoespaciocurricular";

            case "curso":
                return "curso";

            case "libro":
                return "libro";

            case "folio":
                return "folio";
        }

        // Match dinámico: "nota X etapa Y"
        Matcher m = NOTA_ETAPA.matcher(s.replaceAll("\\s+", " "));
        if (m.matches()) {
            String n = m.group(1);
            String e = m.group(2);
            return "nota" + n + "etapa" + e;
        }

        // Fallback genérico: quitar espacios para comparar más fácil
        return s.replace(" ", "");
    }
}