package com.grup14.luterano.controller;


import com.grup14.luterano.dto.AsistenciaAlumnoDto;
import com.grup14.luterano.exeptions.AsistenciaException;
import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoBulkRequest;
import com.grup14.luterano.request.asistenciaAlumno.AsistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.asistenciaAlumno.AsistenciaAlumnoResponseList;
import com.grup14.luterano.service.AsistenciaAlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/asistencia/alumnos")
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
    public ResponseEntity<AsistenciaAlumnoResponseList> actualizarAsistenciaAlumno(
            @RequestBody @Validated AsistenciaAlumnoUpdateRequest req) {
        try {
            AsistenciaAlumnoDto dto = asistenciaAlumnoService.actualizarAsistenciaAlumno(req);
            return ResponseEntity.ok(
                    AsistenciaAlumnoResponseList.builder()
                            .items(List.of(dto))
                            .code(0)
                            .mensaje("OK")
                            .build()
            );
        } catch (AsistenciaException e) {
            return ResponseEntity.status(422).body(
                    AsistenciaAlumnoResponseList.builder()
                            .items(List.of())
                            .code(-1)
                            .mensaje("Error al actualizar asistencia: " + e.getMessage())
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
}