package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.AsistenciaException;
import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoBulkRequest;
import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponse;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponseList;
import com.grup14.luterano.service.AsistenciaAlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/asistencia/alumnos")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
@Tag(
        name = "Asistencia Alumno Controller",
        description = "Controlador encargado de la gestión de asistencias de alumnos. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR o PRECEPTOR."
)
@RequiredArgsConstructor
public class AsistenciaAlumnoController {

    private final AsistenciaAlumnoService asistenciaAlumnoService;

    @PostMapping("/curso")
    @Operation(summary = "Registra/actualiza asistencia del curso en una fecha")
    public ResponseEntity<AsistenciaAlumnoResponseList> registrarAsistenciaCurso(
            @RequestBody @Validated AsistenciaAlumnoBulkRequest req) {
        try {
            var resp = asistenciaAlumnoService.registrarAsistenciaCurso(req);
            return ResponseEntity.ok(resp);
        } catch (AsistenciaException e) {
            return ResponseEntity.status(422).body(
                    AsistenciaAlumnoResponseList.builder()
                            .items(List.of())
                            .code(-1)
                            .mensaje("Error al registrar asistencia del curso: " + e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AsistenciaAlumnoResponseList.builder()
                            .items(List.of())
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza la asistencia puntual de un alumno (upsert por alumnoId+fecha)")
    public ResponseEntity<AsistenciaAlumnoResponse> actualizarAsistenciaAlumno(
            @RequestBody @Validated AsistenciaAlumnoUpdateRequest req) {
        try {
            return ResponseEntity.ok(asistenciaAlumnoService.actualizarAsistenciaAlumno(req));

        } catch (AsistenciaException e) {
            return ResponseEntity.status(422).body(
                    AsistenciaAlumnoResponse.builder()
                            .code(-1)
                            .mensaje("Error al actualizar asistencia: " + e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AsistenciaAlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Lista la asistencia de un curso en una fecha")
    public ResponseEntity<AsistenciaAlumnoResponseList> listarAsistenciaCursoPorFecha(
            @PathVariable Long cursoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            var resp = asistenciaAlumnoService.listarAsistenciaCursoPorFecha(cursoId, fecha);
            return ResponseEntity.ok(resp);
        } catch (AsistenciaException e) {
            return ResponseEntity.status(422).body(
                    AsistenciaAlumnoResponseList.builder()
                            .items(List.of())
                            .code(-1)
                            .mensaje("Error al listar asistencia: " + e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AsistenciaAlumnoResponseList.builder()
                            .items(List.of())
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping("/{alumnoId}/historial")
    @Operation(summary = "Elimina todo el historial de asistencias del alumno")
    public ResponseEntity<Long> resetHistorialCompleto(
            @PathVariable Long alumnoId) {
        try {
            var resp = asistenciaAlumnoService.resetHistorialCompleto(alumnoId);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0L);
        }
    }
}