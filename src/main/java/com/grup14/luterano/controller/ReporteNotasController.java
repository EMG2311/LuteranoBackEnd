package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.ReporteNotasException;
import com.grup14.luterano.response.reporteNotas.CalificacionesAlumnoAnioResponse;
import com.grup14.luterano.response.reporteNotas.CalificacionesCursoAnioResponse;
import com.grup14.luterano.service.ReporteNotasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporteNotas")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') ")
@Tag(
        name = "reporteNotas Controller",
        description = "Controlador encargado de devolver reporteNotas. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR, PRECEPTOR y DOCENTE"
)
@AllArgsConstructor
public class ReporteNotasController {
    private final ReporteNotasService reporteNotasService;


    @GetMapping("/alumnos/{alumnoId}/notas/resumen")
    @Operation(
            summary = "Resumen de notas por materia (E1/E2/PG) para un año",
            description = "Agrupa notas por materia y calcula promedios de cada etapa y general."
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') or hasRole('DOCENTE')")
    public ResponseEntity<CalificacionesAlumnoAnioResponse> resumenNotasAlumnoPorAnio(
            @PathVariable Long alumnoId,
            @RequestParam int anio
    ) {
        try {
            var res = reporteNotasService.listarResumenPorAnio(alumnoId, anio);
            return ResponseEntity.ok(res);

        } catch (ReporteNotasException e) {
            return ResponseEntity.status(422).body(
                    CalificacionesAlumnoAnioResponse.builder().code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionesAlumnoAnioResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build()
            );
        }
    }

    @GetMapping("/curso/{cursoId}/notas/resumen")
    @Operation(
            summary = "Resumen de notas por materia (E1/E2/PG) para un año",
            description = "Agrupa notas por materia y calcula promedios de cada etapa y general."
    )
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') or hasRole('DOCENTE')")
    public ResponseEntity<CalificacionesCursoAnioResponse> resumenNotasCursoPorAnio(
            @PathVariable Long cursoId,
            @RequestParam int anio
    ) {
        try {
            var res = reporteNotasService.listarResumenCursoPorAnio(cursoId, anio);
            return ResponseEntity.ok(res);

        } catch (ReporteNotasException e) {
            return ResponseEntity.status(422).body(
                    CalificacionesCursoAnioResponse.builder().code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionesCursoAnioResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build()
            );
        }
    }
}
