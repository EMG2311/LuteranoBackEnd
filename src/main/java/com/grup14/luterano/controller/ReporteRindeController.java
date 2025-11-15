package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.ReporteRindeException;
import com.grup14.luterano.response.reporteRinden.ReporteRindenResponse;
import com.grup14.luterano.service.ReporteRindenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporteRinde")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') ")
@Tag(
        name = "reporteRinde Controller",
        description = "Controlador encargado de devolver reporteRinde. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR, PRECEPTOR y DOCENTE"
)
@AllArgsConstructor
public class ReporteRindeController {
    private final ReporteRindenService reporteRindenService;


    @GetMapping("/cursos/{cursoId}/rinden")
    @Operation(
            summary = "Alumnos que rinden (Dic/Feb) por curso",
            description = "Devuelve sólo los alumnos del curso que deben rendir COLOQUIO (si aprueban sólo una etapa) " +
                    "o EXAMEN (si no aprueban ninguna), por cada materia del curso. Incluye E1, E2 y PG. " +
                    "Si incluirPrevias=true, también incluye alumnos de otros cursos con materias desaprobadas (previas)."
    )
    public ResponseEntity<ReporteRindenResponse> rindenPorCurso(
            @PathVariable Long cursoId,
            @RequestParam int anio,
            @RequestParam(required = false, defaultValue = "false") boolean incluirPrevias
    ) {
        try {
            var res = reporteRindenService.listarRindenPorCurso(cursoId, anio, incluirPrevias);
            return ResponseEntity.ok(res);

        } catch (ReporteRindeException e) {
            return ResponseEntity.status(422).body(
                    ReporteRindenResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ReporteRindenResponse.builder()
                            .code(-2)
                            .mensaje("Error inesperado: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/cursos/{cursoId}/todos")
    @Operation(
            summary = "TODOS los alumnos del curso (incluye aprobados)",
            description = "Devuelve todos los alumnos del curso incluyendo los promocionados y aprobados por mesa. " +
                    "Útil para mostrar el estado académico completo."
    )
    public ResponseEntity<ReporteRindenResponse> todosLosAlumnosPorCurso(
            @PathVariable Long cursoId,
            @RequestParam int anio
    ) {
        try {
            var res = reporteRindenService.listarTodosLosAlumnosPorCurso(cursoId, anio);
            return ResponseEntity.ok(res);

        } catch (ReporteRindeException e) {
            return ResponseEntity.status(422).body(
                    ReporteRindenResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ReporteRindenResponse.builder()
                            .code(-2)
                            .mensaje("Error inesperado: " + e.getMessage())
                            .build()
            );
        }
    }
}
