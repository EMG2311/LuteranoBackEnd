package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteAnual.ReporteAnualAlumnoResponse;
import com.grup14.luterano.service.ReporteAnualService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporteAnual")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') or hasRole('DOCENTE') ")
@Tag(name = "Reporte Anual", description = "Informe anual por alumno con calificaciones, promedios, inasistencias y previas")
@RequiredArgsConstructor
public class ReporteAnualController {
    private final ReporteAnualService reporteAnualService;

    @GetMapping("/alumnos/{alumnoId}")
    @Operation(summary = "Informe anual por alumno", description = "Devuelve un JSON con resumen de rendimiento, inasistencias y previas para el año dado")
    public ResponseEntity<ReporteAnualAlumnoResponse> informeAnualAlumno(
            @PathVariable Long alumnoId,
            @RequestParam int anio
    ) {
        try {
            var res = reporteAnualService.informeAnualAlumno(alumnoId, anio);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422)
                    .body(ReporteAnualAlumnoResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReporteAnualAlumnoResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build());
        }
    }
}
