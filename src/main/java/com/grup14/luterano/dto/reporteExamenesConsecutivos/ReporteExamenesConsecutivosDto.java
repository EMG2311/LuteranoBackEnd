package com.grup14.luterano.dto.reporteExamenesConsecutivos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteExamenesConsecutivosDto {
    private Long alumnoId;
    private String alumnoNombre;
    private String alumnoApellido;
    private String nombreCompleto;
    
    private Long materiaId;
    private String materiaNombre;
    
    private Long cursoId;
    private String cursoNombre;
    private Integer anio;
    private String division;
    
    private Integer primeraNota;
    private Integer etapaPrimeraNota;
    private Integer numeroPrimeraNota;
    
    private Integer segundaNota;
    private Integer etapaSegundaNota;
    private Integer numeroSegundaNota;
    
    private String descripcionConsecutivo; // "4ta nota Etapa 1 y 1ra nota Etapa 2"
    private String estadoRiesgo; // "CRÍTICO", "ALTO", "MEDIO", "EMERGENCIA"
    private Integer cantidadConsecutivas; // Cantidad total de exámenes consecutivos en la secuencia
    private String notasSecuencia; // "3,4,2,5" - todas las notas de la secuencia para debugging
    
    // NUEVOS CAMPOS: información del docente a cargo de la materia/curso
    private Long docenteId;
    private String docenteNombre;
    private String docenteApellido;
    private String docenteNombreCompleto;
}