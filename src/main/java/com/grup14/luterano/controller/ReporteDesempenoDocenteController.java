package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteDesempeno.ReporteDesempenoResponse;
import com.grup14.luterano.service.ReporteDesempenoDocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reportes/desempeno-docente")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reporte Desempeño Docente", description = "Reportes de análisis de tasas de aprobación/reprobación por docente y materia")
@PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
public class ReporteDesempenoDocenteController {

    private final ReporteDesempenoDocenteService reporteService;

    @GetMapping("/{cicloLectivoAnio}")
    @Operation(
            summary = "Reporte completo de desempeño docente",
            description = "Genera un análisis completo de tasas de aprobación/reprobación por docente y materia para un ciclo lectivo específico. " +
                    "Permite comparar el rendimiento entre docentes de la misma materia e identificar patrones de desempeño."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo lectivo no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - rol insuficiente")
    })
    public ResponseEntity<ReporteDesempenoResponse> generarReporteCompleto(
            @Parameter(description = "Año del ciclo lectivo a analizar", example = "2024")
            @PathVariable Integer cicloLectivoAnio) {

        log.info("Generando reporte de desempeño docente para ciclo: {}", cicloLectivoAnio);

        try {
            ReporteDesempenoResponse response = reporteService.generarReporteDesempeno(cicloLectivoAnio);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al generar reporte de desempeño: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ReporteDesempenoResponse.builder()
                            .code(-1)
                            .mensaje("Error al generar reporte: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{cicloLectivoAnio}/materia/{materiaId}")
    @Operation(
            summary = "Reporte de desempeño por materia específica",
            description = "Analiza el desempeño de todos los docentes que dictan una materia específica en un ciclo lectivo. " +
                    "Útil para comparar metodologías y resultados entre docentes de la misma materia."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte por materia generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Materia o ciclo lectivo no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - rol insuficiente")
    })
    public ResponseEntity<ReporteDesempenoResponse> generarReportePorMateria(
            @Parameter(description = "Año del ciclo lectivo a analizar", example = "2024")
            @PathVariable Integer cicloLectivoAnio,
            @Parameter(description = "ID de la materia a analizar", example = "1")
            @PathVariable Long materiaId) {

        log.info("Generando reporte de desempeño para materia {} en ciclo: {}", materiaId, cicloLectivoAnio);

        try {
            ReporteDesempenoResponse response = reporteService.generarReportePorMateria(cicloLectivoAnio, materiaId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al generar reporte por materia: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ReporteDesempenoResponse.builder()
                            .code(-1)
                            .mensaje("Error al generar reporte: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{cicloLectivoAnio}/docente/{docenteId}")
    @Operation(
            summary = "Reporte de desempeño por docente específico",
            description = "Analiza el desempeño de un docente específico en todas las materias que dictó durante un ciclo lectivo. " +
                    "Útil para evaluaciones individuales y seguimiento del rendimiento docente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte por docente generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Docente o ciclo lectivo no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - rol insuficiente")
    })
    public ResponseEntity<ReporteDesempenoResponse> generarReportePorDocente(
            @Parameter(description = "Año del ciclo lectivo a analizar", example = "2024")
            @PathVariable Integer cicloLectivoAnio,
            @Parameter(description = "ID del docente a analizar", example = "5")
            @PathVariable Long docenteId) {

        log.info("Generando reporte de desempeño para docente {} en ciclo: {}", docenteId, cicloLectivoAnio);

        try {
            ReporteDesempenoResponse response = reporteService.generarReportePorDocente(cicloLectivoAnio, docenteId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al generar reporte por docente: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ReporteDesempenoResponse.builder()
                            .code(-1)
                            .mensaje("Error al generar reporte: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{cicloLectivoAnio}/curso/{cursoId}")
    @Operation(
            summary = "Reporte de desempeño por curso específico",
            description = "Analiza el desempeño de todos los docentes que dictaron clases en un curso específico durante un ciclo lectivo. " +
                    "Útil para evaluar el rendimiento académico general de un curso y identificar materias o docentes que requieran atención."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte por curso generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Curso o ciclo lectivo no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - rol insuficiente")
    })
    public ResponseEntity<ReporteDesempenoResponse> generarReportePorCurso(
            @Parameter(description = "Año del ciclo lectivo a analizar", example = "2024")
            @PathVariable Integer cicloLectivoAnio,
            @Parameter(description = "ID del curso a analizar", example = "3")
            @PathVariable Long cursoId) {

        log.info("Generando reporte de desempeño para curso {} en ciclo: {}", cursoId, cicloLectivoAnio);

        try {
            ReporteDesempenoResponse response = reporteService.generarReportePorCurso(cicloLectivoAnio, cursoId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al generar reporte por curso: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ReporteDesempenoResponse.builder()
                            .code(-1)
                            .mensaje("Error al generar reporte: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{cicloLectivoAnio}/notas-individuales")
    @Operation(
            summary = "Reporte de análisis de notas individuales por docente",
            description = "Analiza las 4 notas individuales de cada etapa para identificar diferencias de rendimiento " +
                    "entre docentes de la misma materia. Útil para detectar casos donde un docente puede necesitar apoyo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reporte de notas individuales generado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Ciclo lectivo no encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - rol insuficiente")
    })
    public ResponseEntity<ReporteDesempenoResponse> generarReporteNotasIndividuales(
            @Parameter(description = "Año del ciclo lectivo a analizar", example = "2024")
            @PathVariable Integer cicloLectivoAnio) {

        log.info("Generando reporte de notas individuales para ciclo: {}", cicloLectivoAnio);

        try {
            ReporteDesempenoResponse response = reporteService.generarReporteNotasIndividuales(cicloLectivoAnio);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al generar reporte de notas individuales: {}", e.getMessage());
            return ResponseEntity.badRequest().body(
                    ReporteDesempenoResponse.builder()
                            .code(-1)
                            .mensaje("Error al generar reporte: " + e.getMessage())
                            .build()
            );
        }
    }
}