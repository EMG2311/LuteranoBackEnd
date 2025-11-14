package com.grup14.luterano.dto.reporteRankingAlumno;

import com.grup14.luterano.dto.CursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlumnoRankingDto {
    private Long alumnoId;
    private String dni;
    private String apellido;
    private String nombre;
    private String nombreCompleto;
    private BigDecimal promedio;
    private Integer posicion;
    private CursoDto curso;
}