package com.grup14.luterano.service;

import com.grup14.luterano.dto.notaFinal.NotaFinalDetalleDto;

public interface NotaFinalService {

    /**
     * Calcula la nota final de una materia para un alumno en un año específico.
     * <p>
     * Lógica:
     * 1. Si tiene mesa de examen → usar notaFinal de la mesa más reciente
     * 2. Si no tiene mesa → usar PG truncado (sin decimales)
     *
     * @param alumnoId  ID del alumno
     * @param materiaId ID de la materia
     * @param anio      Año a evaluar
     * @return Nota final calculada (null si no hay datos suficientes)
     */
    Integer calcularNotaFinal(Long alumnoId, Long materiaId, int anio);

    /**
     * Obtiene la nota final con información detallada sobre el origen.
     *
     * @param alumnoId  ID del alumno
     * @param materiaId ID de la materia
     * @param anio      Año a evaluar
     * @return DTO con nota final y detalles del origen
     */
    NotaFinalDetalleDto obtenerNotaFinalDetallada(Long alumnoId, Long materiaId, int anio);
}