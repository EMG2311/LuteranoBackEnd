package com.grup14.luterano.dto.reporteDesempeno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoDocenteDto {
    
    private Long docenteId;
    private String apellidoDocente;
    private String nombreDocente;
    private String nombreCompletoDocente;
    
    private Long materiaId;
    private String nombreMateria;
    
    private Long cursoId;
    private Integer anio;
    private String nivel;
    private String division;
    private String cursoCompleto;
    
    private Integer totalAlumnos;
    private Integer alumnosAprobados;
    private Integer alumnosDesaprobados;
    
    private BigDecimal porcentajeAprobacion;
    private BigDecimal porcentajeReprobacion;
    
    private BigDecimal promedioGeneral;
    private BigDecimal notaMinima;
    private BigDecimal notaMaxima;
    
    // Estadísticas adicionales
    private Integer cicloLectivoAnio;
    private String estadoAnalisis; // EXCELENTE, BUENO, REGULAR, PREOCUPANTE
}