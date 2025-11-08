package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.ReporteLibreException;
import com.grup14.luterano.response.ReporteLibresResponse;
import com.grup14.luterano.service.ReporteLibreService;
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
@RequestMapping("/reporteLibres")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
@Tag(name = "Reporte Libres Controller", description = "Devuelve alumnos libres por inasistencias (>25).")
@RequiredArgsConstructor
public class ReporteLibresController {

    private final ReporteLibreService reporteLibresService;

    @GetMapping("/libres")
    @Operation(summary = "Libres (JSON)",
            description = "Si se pasa cursoId filtra por curso; si no, trae todos los libres del colegio.")
    public ResponseEntity<ReporteLibresResponse> libresJson(
            @RequestParam Integer anio,
            @RequestParam(required = false) Long cursoId
    ) {
        try {
            var res = reporteLibresService.listarLibres(anio, cursoId);
            return ResponseEntity.ok(res);

        } catch (ReporteLibreException e) {
            return ResponseEntity.status(422).body(
                    ReporteLibresResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ReporteLibresResponse.builder()
                            .code(-2)
                            .mensaje("Error inesperado: " + e.getMessage())
                            .build()
            );
        }
    }
}