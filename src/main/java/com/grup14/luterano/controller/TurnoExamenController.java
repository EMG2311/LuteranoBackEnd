package com.grup14.luterano.controller;

import com.grup14.luterano.dto.TurnoDto;
import com.grup14.luterano.exeptions.TurnoExamenException;
import com.grup14.luterano.response.turnoExamen.TurnoListResponse;
import com.grup14.luterano.response.turnoExamen.TurnoResponse;
import com.grup14.luterano.service.TurnoExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/turnos")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@Tag(name = "Turno Examen Controller", description = "CRUD de turnos de examen")
@RequiredArgsConstructor
public class TurnoExamenController {

    private final TurnoExamenService service;

    @GetMapping
    @Operation(summary = "Listar turnos (opcional por año)")
    public ResponseEntity<TurnoListResponse> listar(@RequestParam(required = false) Integer anio) {
        try {
            return ResponseEntity.ok(service.listar(anio));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TurnoListResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @PostMapping
    @Operation(summary = "Crear turno")
    public ResponseEntity<TurnoResponse> crear(@RequestBody TurnoDto dto) {
        try {
            return ResponseEntity.ok(service.crear(dto));
        } catch (TurnoExamenException e) {
            return ResponseEntity.status(422)
                    .body(TurnoResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(TurnoResponse.builder().code(-2).mensaje(e.getMessage()).build());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar turno")
    public ResponseEntity<TurnoResponse> actualizar(@PathVariable Long id, @RequestBody TurnoDto dto) {
        try {
            return ResponseEntity.ok(service.actualizar(id, dto));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(TurnoResponse.builder().code(-2).mensaje(e.getMessage()).build());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar turno")
    public ResponseEntity<TurnoResponse> eliminar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.eliminar(id));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(TurnoResponse.builder().code(-2).mensaje(e.getMessage()).build());
        }
    }
}
