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
    private Integer casosCriticos; // Ambas notas <= 4
    private Integer casosAltos; // Una nota <= 4, otra <= 6
    private Integer casosMedios; // Ambas notas <= 6
    
    // Resumen por materia
    private List<ResumenPorMateriaDto> resumenPorMateria;
    
    // Recomendaciones
    private List<String> recomendaciones;
    
    private Integer code;
    private String mensaje;
}