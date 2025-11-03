package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteDisponibilidad.DocenteDisponibilidadResponse;
import com.grup14.luterano.service.ReporteDisponibilidadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporteDisponibilidad")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') or hasRole('DOCENTE') ")
@Tag(name = "Reporte Disponibilidad Docente", description = "Horarios ocupados por docente y materias dictadas")
@RequiredArgsConstructor
public class ReporteDisponibilidadController {

    private final ReporteDisponibilidadService servicio;

    @GetMapping("/docentes/{docenteId}")
    @Operation(summary = "Disponibilidad de un docente", description = "Devuelve sus bloques ocupados por día, materias y horas ocupadas")
    public ResponseEntity<DocenteDisponibilidadResponse> disponibilidadDocente(@PathVariable Long docenteId) {
        try {
            var res = servicio.disponibilidadDocente(docenteId);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422)
                    .body(DocenteDisponibilidadResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DocenteDisponibilidadResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build());
        }
    }
}
