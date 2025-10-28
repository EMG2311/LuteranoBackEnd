package com.grup14.luterano.dto.reporteAnual;

import com.grup14.luterano.dto.CursoDto;
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
public class ReporteAnualAlumnoDto {
    private Long alumnoId;
    private Integer anio;

    private String dni;
    private String legajo; // Nota: si no existe en el modelo, usamos el DNI como legajo por ahora
    private String apellido;
    private String nombre;

    private CursoDto curso; // curso del año (según HistorialCurso vigente)

    private List<MateriaAnualDetalleDto> materias;

    private BigDecimal promedioFinalCurso; // HistorialCurso.promedio
    private InasistenciasResumenDto inasistencias;

    // Materias en condición de "previas" (según estado desaprobada)
    private List<Long> materiasPreviasIds;
}
