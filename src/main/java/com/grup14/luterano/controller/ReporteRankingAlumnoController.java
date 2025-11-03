package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosColegioResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingAlumnosCursoResponse;
import com.grup14.luterano.response.reporteRankingAlumno.RankingTodosCursosResponse;
import com.grup14.luterano.service.ReporteRankingAlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/reportes/ranking-alumnos")
@RequiredArgsConstructor
public class ReporteRankingAlumnoController {

    private final ReporteRankingAlumnoService rankingService;

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Obtiene el ranking de alumnos con mejor promedio de un curso específico")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<RankingAlumnosCursoResponse> rankingAlumnosPorCurso(
            @PathVariable Long cursoId,
            @RequestParam int anio) {
        try {
            RankingAlumnosCursoResponse response = rankingService.rankingAlumnosPorCurso(cursoId, anio);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(RankingAlumnosCursoResponse.builder()
                    .ranking(Collections.emptyList())
                    .cursoId(cursoId)
                    .cursoNombre("")
                    .totalAlumnos(0)
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(RankingAlumnosCursoResponse.builder()
                    .ranking(Collections.emptyList())
                    .cursoId(cursoId)
                    .cursoNombre("")
                    .totalAlumnos(0)
                    .code(-2)
                    .mensaje("Error no controlado: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/colegio")
    @Operation(summary = "Obtiene el ranking de alumnos con mejor promedio de todo el colegio")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<RankingAlumnosColegioResponse> rankingAlumnosColegio(
            @RequestParam int anio) {
        try {
            RankingAlumnosColegioResponse response = rankingService.rankingAlumnosColegio(anio);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(RankingAlumnosColegioResponse.builder()
                    .ranking(Collections.emptyList())
                    .totalAlumnos(0)
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(RankingAlumnosColegioResponse.builder()
                    .ranking(Collections.emptyList())
                    .totalAlumnos(0)
                    .code(-2)
                    .mensaje("Error no controlado: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/todos-cursos")
    @Operation(summary = "Obtiene todos los cursos con el ranking de sus mejores alumnos")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<RankingTodosCursosResponse> rankingTodosCursos(
            @RequestParam int anio) {
        try {
            RankingTodosCursosResponse response = rankingService.rankingTodosCursos(anio);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(RankingTodosCursosResponse.builder()
                    .cursosRanking(Collections.emptyList())
                    .totalCursos(0)
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(RankingTodosCursosResponse.builder()
                    .cursosRanking(Collections.emptyList())
                    .totalCursos(0)
                    .code(-2)
                    .mensaje("Error no controlado: " + e.getMessage())
                    .build());
        }
    }
}