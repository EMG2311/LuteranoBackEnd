package com.grup14.luterano.controller;

import com.grup14.luterano.response.reporteTardanza.ReporteTardanzasResponseList;
import com.grup14.luterano.service.ReporteTardanzaService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportesTardanza")
@RequiredArgsConstructor
@Validated
public class ReporteTardanzaController {

    private final ReporteTardanzaService service;

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Reporte de tardanzas por curso",
            description = "Devuelve alumno, curso y cantidad de llegadas tarde. " +
                    "Parámetros opcionales: desde, hasta (YYYY-MM-DD) y limit (top N).")
    public ResponseEntity<ReporteTardanzasResponseList> reportePorCurso(
            @PathVariable Long cursoId,
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) Integer limit
    ) {
        try {
            return ResponseEntity.ok(service.listarPorCurso(cursoId, desde, hasta, limit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ReporteTardanzasResponseList.builder()
                            .items(java.util.List.of())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/todos")
    @Operation(summary = "Reporte de tardanzas de todos los cursos",
            description = "Devuelve alumno, curso y cantidad de llegadas tarde. " +
                    "Parámetros opcionales: desde, hasta (YYYY-MM-DD) y limit (top N).")
    public ResponseEntity<ReporteTardanzasResponseList> reporteTodos(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(required = false) Integer limit
    ) {
        try {
            return ResponseEntity.ok(service.listarTodos(desde, hasta, limit));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ReporteTardanzasResponseList.builder()
                            .items(java.util.List.of())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build()
            );
        }
    }
}
