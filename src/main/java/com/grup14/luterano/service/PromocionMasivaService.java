package com.grup14.luterano.service;

import com.grup14.luterano.request.promocion.PromocionMasivaRequest;
import com.grup14.luterano.response.promocion.PromocionMasivaResponse;

public interface PromocionMasivaService {

    /**
     * Ejecuta la promoción masiva de alumnos según las reglas definidas:
     * - Menos de 3 materias desaprobadas: promociona al curso siguiente
     * - 3 o más materias desaprobadas: repite año
     * - 6to año: egresa
     * - Controla límite de repeticiones
     */
    PromocionMasivaResponse ejecutarPromocionMasiva(PromocionMasivaRequest request);
}