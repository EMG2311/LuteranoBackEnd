package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.AsistenciaDocenteException;
import com.grup14.luterano.request.AsistenciaDocenteUpdateRequest;
import com.grup14.luterano.response.asistenciaDocente.AsistenciaDocenteResponse;
import com.grup14.luterano.response.asistenciaDocente.AsistenciaDocenteResponseList;
import com.grup14.luterano.service.AsistenciaDocenteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;

@RestController
@RequestMapping("/asistencia-docente")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
@RequiredArgsConstructor
@Tag(
        name = "Asistencia Docente Controller",
        description = "Controlador encargado de la gestión de asistencias de docentes. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR o PRECEPTOR."
)
public class AsistenciaDocenteController {

    private final AsistenciaDocenteService asistenciaDocenteService;

    @PostMapping("/upsert")
    @Operation(summary = "Crea o actualiza una asistencia de docente (upsert)")
    public ResponseEntity<AsistenciaDocenteResponse> upsert(@RequestBody AsistenciaDocenteUpdateRequest req) {
        try {
            return ResponseEntity.ok(asistenciaDocenteService.upsert(req));
        } catch (AsistenciaDocenteException e) {
            return ResponseEntity.status(422).body(
                    AsistenciaDocenteResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .asistenciaDocenteDto(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AsistenciaDocenteResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .asistenciaDocenteDto(null)
                            .build()
            );
        }
    }

    @GetMapping("/docente/{docenteId}")
    @Operation(summary = "Lista asistencias de un docente en una fecha")
    public ResponseEntity<AsistenciaDocenteResponseList> listarPorDocenteYFecha(
            @PathVariable Long docenteId,
            @RequestParam
            LocalDate fecha) {
        try {
            return ResponseEntity.ok(asistenciaDocenteService.listarPorDocenteYFecha(docenteId, fecha));
        } catch (AsistenciaDocenteException e) {
            return ResponseEntity.status(422).body(
                    AsistenciaDocenteResponseList.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .items(Collections.emptyList())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AsistenciaDocenteResponseList.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .items(Collections.emptyList())
                            .build()
            );
        }
    }

    @GetMapping("/fecha")
    @Operation(summary = "Lista asistencias de todos los docentes en una fecha")
    public ResponseEntity<AsistenciaDocenteResponseList> listarPorFecha(
            @RequestParam
            LocalDate fecha) {
        try {
            return ResponseEntity.ok(asistenciaDocenteService.listarPorFecha(fecha));
        } catch (AsistenciaDocenteException e) {
            return ResponseEntity.status(422).body(
                    AsistenciaDocenteResponseList.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .items(Collections.emptyList())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AsistenciaDocenteResponseList.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .items(Collections.emptyList())
                            .build()
            );
        }
    }
}