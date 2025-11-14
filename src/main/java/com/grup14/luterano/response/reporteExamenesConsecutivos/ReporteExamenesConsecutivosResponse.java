package com.grup14.luterano.response.reporteExamenesConsecutivos;

import com.grup14.luterano.dto.reporteExamenesConsecutivos.ReporteExamenesConsecutivosDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteExamenesConsecutivosResponse {
    private Integer cicloLectivoAnio;
    private String nombreCicloLectivo;
    
    private Integer totalAlumnosEnRiesgo;
    private Integer totalMateriasAfectadas;
    private Integer totalCursosAfectados;
    
    // Casos detectados
    private List<ReporteExamenesConsecutivosDto> casosDetectados;
    
    // Estadísticas por nivel de riesgo
    private Integer casosEmergencia; // 4+ exámenes consecutivos
    private Integer casosCriticos; // 3 exámenes consecutivos
    private Integer casosAltos; // 2 exámenes consecutivos  
    private Integer casosMedios; // Casos edge
    
    // Resumen por materia
    private List<ResumenPorMateriaDto> resumenPorMateria;
    
    // Recomendaciones
    private List<String> recomendaciones;
    
    private Integer code;
    private String mensaje;
}