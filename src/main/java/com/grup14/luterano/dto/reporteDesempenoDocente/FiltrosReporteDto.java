package com.grup14.luterano.dto.reporteDesempenoDocente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data @Builder @NoArgsConstructor  @AllArgsConstructor
public class FiltrosReporteDto {

    // Filtros de Identificadores (Los que vienen del Request)
    private Long docenteId;
    private Long materiaId;
    private Long cursoId;
    private Integer cicloLectivoAnioFiltro;
    // Rango de Fechas
    private LocalDate rangoFechaDesde;
    private LocalDate rangoFechaHasta;


    // Nombres Descriptivos (Para mostrar al usuario)
    private String docenteNombreCompleto; // Ejemplo: "Marta Sánchez" o "-Todos-"
    private String materiaNombre; // Ejemplo: "Matemáticas" o "-Todos-"
    private String cursoNombre;   // Ejemplo: "Cuarto B" o "-Todos-"

    // --- Indicador para el front-end ---
    private String periodoDescripcion; // para indicar ej. si es "ciclo lectivo 2025" o "Desde dd/mm/yyyy hasta dd/mm/yyyy"

}
