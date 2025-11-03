package com.grup14.luterano.mappers;

import com.grup14.luterano.entities.enums.Nivel;

import java.util.Locale;

public final class NivelMapper {
    private NivelMapper() {
    }

    public static Nivel fromPlanEstudio(String planRaw) {
        if (planRaw == null) return null;
        String s = planRaw.trim().toUpperCase(Locale.ROOT);
        if (s.contains("BÁSICO") || s.contains("BASICO")) return Nivel.BASICO;
        if (s.contains("ORIENTADO")) return Nivel.ORIENTADO;
        return null;
    }
}
