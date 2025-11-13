package com.grup14.luterano.response.reporteDesempeno;

import com.grup14.luterano.dto.ReporteDesempenoDocenteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDesempenoDocenteResponse {
    private int code;
    private String mensaje;
    private int anio;
    
    // Datos del reporte
    private List<ReporteDesempenoDocenteDto> docentes;
    
    // Estadísticas generales
    private int totalDocentes;
    private int docentesNormal;
    private int docentesAtencion;
    private int docentesCritico;
    
    // Resumen de rendimiento
    private Double promedioGeneralInstitucion;
    private String materiaConMayorProblema;
    private String materiaConMejorRendimiento;
}