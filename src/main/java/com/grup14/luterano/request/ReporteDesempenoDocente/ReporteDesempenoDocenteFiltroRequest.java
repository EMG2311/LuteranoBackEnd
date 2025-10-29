package com.grup14.luterano.request.ReporteDesempenoDocente;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReporteDesempenoDocenteFiltroRequest {

    // Prioridad 1: Período oficial (si se envía, se ignoran las fechas)
    private Integer cicloLectivoAnio;

    // Prioridad 2: Rango personalizado (solo se usa si no se envía el año)
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    // Otros filtros
    private Long docenteId;
    private Long materiaId;
    private Long cursoId;


}
