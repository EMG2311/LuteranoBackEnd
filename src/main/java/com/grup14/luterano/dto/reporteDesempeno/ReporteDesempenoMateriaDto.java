package com.grup14.luterano.dto.reporteDesempeno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoMateriaDto {
    
    private Long materiaId;
    private String nombreMateria;
    
    // Estadísticas generales de la materia
    private Integer totalDocentes;
    private Integer totalAlumnos;
    private Integer totalCursos;
    
    private BigDecimal promedioAprobacionMateria;
    private BigDecimal promedioReprobacionMateria;
    private BigDecimal promedioGeneralMateria;
    
    // Análisis por docente
    private List<ReporteDesempenoDocenteDto> resultadosPorDocente;
    
    // Docente con mejor y peor desempeño
    private ReporteDesempenoDocenteDto mejorDocente;
    private ReporteDesempenoDocenteDto peorDocente;
    
    // Rango de variación
    private BigDecimal rangoAprobacion; // diferencia entre mejor y peor
    private BigDecimal desviacionEstandar;
}