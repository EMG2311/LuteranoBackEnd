package com.grup14.luterano.response.reporteRankingAlumno;

import com.grup14.luterano.dto.reporteRankingAlumno.AlumnoRankingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankingAlumnosCursoResponse {
    private List<AlumnoRankingDto> ranking;
    private Long cursoId;
    private String cursoNombre;
    private Integer totalAlumnos;
    private int code;
    private String mensaje;
}