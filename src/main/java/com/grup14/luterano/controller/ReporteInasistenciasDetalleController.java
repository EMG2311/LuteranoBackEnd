package com.grup14.luterano.controller;

import com.grup14.luterano.response.asistenciaAlumno.ReporteInasistenciasDetalleResponse;
import com.grup14.luterano.service.ReporteInasistenciasDetalleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporte/inasistencias")
@RequiredArgsConstructor
@Tag(name = "Reporte de Inasistencias", description = "Totales y detalle de inasistencias, justificadas y no justificadas")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
public class ReporteInasistenciasDetalleController {

    private final ReporteInasistenciasDetalleService service;

    @GetMapping("/curso/{cursoId}")
    @Operation(
            summary = "Inasistencias por curso",
            description = "Devuelve todos los alumnos del curso, con totales de inasistencias (justificadas y no justificadas) y detalle día por día."
    )
    public ResponseEntity<ReporteInasistenciasDetalleResponse> porCurso(
            @PathVariable Long cursoId,
            @RequestParam Integer anio
    ) {
        var res = service.inasistenciasDetalle(anio, cursoId, null);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/alumno/{alumnoId}")
    @Operation(
            summary = "Inasistencias por alumno",
            description = "Devuelve el total de inasistencias (justificadas y no justificadas) y el detalle día por día para un alumno."
    )
    public ResponseEntity<ReporteInasistenciasDetalleResponse> porAlumno(
            @PathVariable Long alumnoId,
            @RequestParam Integer anio
    ) {
        var res = service.inasistenciasDetalle(anio, null, alumnoId);
        return ResponseEntity.ok(res);
    }
}
