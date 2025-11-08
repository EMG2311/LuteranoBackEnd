package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteHistorialAlumno.ReporteHistorialAlumnoResponse;
import com.grup14.luterano.service.ReporteHistorialAlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes/historial-alumno")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
@Tag(name = "Reporte Historial Alumno", description = "Generación de reportes del historial académico completo de alumnos")
public class ReporteHistorialAlumnoController {

    private final ReporteHistorialAlumnoService service;

    @GetMapping("/completo/{alumnoId}")
    @Operation(
            summary = "Historial académico completo de un alumno",
            description = "Genera un reporte completo con todas las notas y calificaciones del alumno " +
                         "a través de todos los ciclos lectivos registrados. Incluye estadísticas, " +
                         "tendencias académicas, logros destacados y áreas de mejora.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Historial generado exitosamente"),
                    @ApiResponse(responseCode = "422", description = "Alumno no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<ReporteHistorialAlumnoResponse> generarHistorialCompleto(
            @Parameter(description = "ID del alumno", required = true, example = "1")
            @PathVariable Long alumnoId) {
        
        try {
            ReporteHistorialAlumnoResponse response = service.generarHistorialCompleto(alumnoId);
            
            if (response.getCode() == -1) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ReporteHistorialAlumnoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReporteHistorialAlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/ciclo/{alumnoId}/{cicloLectivoId}")
    @Operation(
            summary = "Historial académico por ciclo específico",
            description = "Genera un reporte del historial académico del alumno para un ciclo lectivo específico. " +
                         "Útil para analizar el rendimiento en un año particular.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Historial por ciclo generado exitosamente"),
                    @ApiResponse(responseCode = "422", description = "Alumno o ciclo no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    public ResponseEntity<ReporteHistorialAlumnoResponse> generarHistorialPorCiclo(
            @Parameter(description = "ID del alumno", required = true, example = "1")
            @PathVariable Long alumnoId,
            @Parameter(description = "ID del ciclo lectivo", required = true, example = "2")
            @PathVariable Long cicloLectivoId) {
        
        try {
            ReporteHistorialAlumnoResponse response = service.generarHistorialPorCiclo(alumnoId, cicloLectivoId);
            
            if (response.getCode() == -1) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ReporteHistorialAlumnoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReporteHistorialAlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error interno del servidor: " + e.getMessage())
                            .build());
        }
    }
}