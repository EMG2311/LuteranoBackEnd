package com.grup14.luterano.dto.reporteRankingAlumno;

import com.grup14.luterano.dto.CursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursoRankingDto {
    private CursoDto curso;
    private List<AlumnoRankingDto> topAlumnos;
    private Integer totalAlumnos;
}