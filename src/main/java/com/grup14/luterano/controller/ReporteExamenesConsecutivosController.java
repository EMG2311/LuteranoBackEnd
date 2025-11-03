package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteExamenesConsecutivos.ReporteExamenesConsecutivosResponse;
import com.grup14.luterano.service.ReporteExamenesConsecutivosService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reportes/examenes-consecutivos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reportes - Exámenes Consecutivos Desaprobados", 
     description = "Endpoints para generar reportes de alumnos que desaprobaron dos exámenes consecutivos de la misma materia")
public class ReporteExamenesConsecutivosController {

    private final ReporteExamenesConsecutivosService reporteService;

    @GetMapping("/institucional/{cicloLectivoAnio}")
    @Operation(
        summary = "Reporte institucional de exámenes consecutivos desaprobados",
        description = "Genera un reporte completo de todos los alumnos que desaprobaron dos exámenes consecutivos de cualquier materia en el año especificado. " +
                     "Incluye estadísticas generales, análisis por materia y recomendaciones de intervención."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "422", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder al reporte"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR')")
    public ResponseEntity<ReporteExamenesConsecutivosResponse> obtenerReporteInstitucional(
            @Parameter(description = "Año del ciclo lectivo", example = "2024")
            @PathVariable Integer cicloLectivoAnio) {
        
        try {
            var reporte = reporteService.generarReporte(cicloLectivoAnio);
            return ResponseEntity.ok(reporte);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build());
        }
    }

    @GetMapping("/materia/{materiaId}/{cicloLectivoAnio}")
    @Operation(
        summary = "Reporte de exámenes consecutivos por materia específica",
        description = "Genera un reporte filtrado para una materia específica, mostrando todos los alumnos " +
                     "que desaprobaron dos exámenes consecutivos de esa materia en el año especificado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "422", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder al reporte"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<ReporteExamenesConsecutivosResponse> obtenerReportePorMateria(
            @Parameter(description = "ID de la materia", example = "1")
            @PathVariable Long materiaId,
            @Parameter(description = "Año del ciclo lectivo", example = "2024")
            @PathVariable Integer cicloLectivoAnio) {
        
        try {
            var reporte = reporteService.generarReportePorMateria(cicloLectivoAnio, materiaId);
            return ResponseEntity.ok(reporte);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build());
        }
    }

    @GetMapping("/curso/{cursoId}/{cicloLectivoAnio}")
    @Operation(
        summary = "Reporte de exámenes consecutivos por curso específico",
        description = "Genera un reporte filtrado para un curso específico, mostrando todos los alumnos " +
                     "del curso que desaprobaron dos exámenes consecutivos de cualquier materia en el año especificado."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "422", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder al reporte"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR')")
    public ResponseEntity<ReporteExamenesConsecutivosResponse> obtenerReportePorCurso(
            @Parameter(description = "ID del curso", example = "1")
            @PathVariable Long cursoId,
            @Parameter(description = "Año del ciclo lectivo", example = "2024")
            @PathVariable Integer cicloLectivoAnio) {
        
        try {
            var reporte = reporteService.generarReportePorCurso(cicloLectivoAnio, cursoId);
            return ResponseEntity.ok(reporte);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build());
        }
    }

    @GetMapping("/resumen/{cicloLectivoAnio}")
    @Operation(
        summary = "Resumen ejecutivo de exámenes consecutivos",
        description = "Genera un resumen ejecutivo con estadísticas clave y métricas principales " +
                     "de los casos de exámenes consecutivos desaprobados, ideal para directivos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen generado exitosamente"),
        @ApiResponse(responseCode = "422", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "403", description = "Sin permisos para acceder al reporte"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<ReporteExamenesConsecutivosResponse> obtenerResumenEjecutivo(
            @Parameter(description = "Año del ciclo lectivo", example = "2024")
            @PathVariable Integer cicloLectivoAnio) {
        
        try {
            // El resumen ejecutivo es el mismo reporte institucional pero con enfoque en estadísticas
            var reporte = reporteService.generarReporte(cicloLectivoAnio);
            return ResponseEntity.ok(reporte);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReporteExamenesConsecutivosResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build());
        }
    }
}