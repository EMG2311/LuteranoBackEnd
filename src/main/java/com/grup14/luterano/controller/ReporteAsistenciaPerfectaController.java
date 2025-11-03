package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteAsistenciaPerfecta.AsistenciaPerfectaResponse;
import com.grup14.luterano.service.ReporteAsistenciaPerfectaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reporteAsistenciaPerfecta")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') or hasRole('DOCENTE') ")
@Tag(name = "Reporte Asistencia Perfecta", description = "Alumnos con asistencia perfecta por año, agrupados por curso")
@RequiredArgsConstructor
public class ReporteAsistenciaPerfectaController {

    private final ReporteAsistenciaPerfectaService service;

    @GetMapping()
    @Operation(summary = "Asistencia perfecta por año", description = "Devuelve por curso la lista de alumnos con asistencia perfecta en el año")
    public ResponseEntity<AsistenciaPerfectaResponse> porAnio(@RequestParam int anio) {
        try {
            var res = service.listarPorAnio(anio);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422)
                    .body(AsistenciaPerfectaResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AsistenciaPerfectaResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build());
        }
    }
}
