package com.grup14.luterano.response.reporteRankingAlumno;

import com.grup14.luterano.dto.reporteRankingAlumno.CursoRankingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RankingTodosCursosResponse {
    private List<CursoRankingDto> cursosRanking;
    private Integer totalCursos;
    private int code;
    private String mensaje;
}