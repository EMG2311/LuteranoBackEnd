package com.grup14.luterano.response.reporteDesempeno;

import com.grup14.luterano.dto.reporteDesempeno.ReporteDesempenoMateriaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoResponse {

    private Integer code;
    private String mensaje;

    // Información del reporte
    private Integer cicloLectivoAnio;
    private String nombreCicloLectivo;

    // Estadísticas globales
    private Integer totalMaterias;
    private Integer totalDocentes;
    private Integer totalAlumnos;
    private Integer totalCursos;

    // Resultados por materia
    private List<ReporteDesempenoMateriaDto> resultadosPorMateria;

    // Análisis general
    private String resumenEjecutivo;
    private List<String> hallazgosImportantes;
    private List<String> recomendaciones;
}