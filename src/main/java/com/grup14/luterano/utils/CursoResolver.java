package com.grup14.luterano.utils;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import com.grup14.luterano.repository.CursoRepository;
import jakarta.persistence.EntityManager;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

public final class CursoResolver {
    private CursoResolver(){}

    public static Integer parseAnio(String gradoRaw) {
        if (gradoRaw == null) return null;
        String s = gradoRaw.toUpperCase(Locale.ROOT);
        if (s.contains("PRIMER")) return 1;
        if (s.contains("SEGUND")) return 2;
        if (s.contains("TERCER")) return 3;
        if (s.contains("CUART"))  return 4;
        if (s.contains("QUINT"))  return 5;
        if (s.contains("SEX"))    return 6;
        var m = Pattern.compile("(\\d+)").matcher(s); // por si viene "1", "1°", etc.
        if (m.find()) return Integer.parseInt(m.group(1));
        return null;
    }

    public static Division parseDivision(String divisionRaw) {
        if (divisionRaw == null || divisionRaw.isBlank()) return null;
        String d = divisionRaw.trim().toUpperCase(Locale.ROOT);
        try { return Division.valueOf(d); } catch (Exception ignore) { return null; }
    }

    /**
     * Busca un curso existente por (anio, división, nivel).
     * Si no existe, lanza IllegalArgumentException.
     */
    public static Curso findOrThrow(
            CursoRepository repo,
            int anio,
            Division division,
            Nivel nivel
    ) {
        return repo.findByAnioAndDivisionAndNivel(anio, division, nivel)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("No existe el curso %s %d %s", nivel, anio, division)
                ));
    }
}