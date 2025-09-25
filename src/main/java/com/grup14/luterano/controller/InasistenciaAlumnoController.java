package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.request.alumno.InasistenciaAlumnoRequest;
import com.grup14.luterano.request.alumno.InasistenciaAlumnoUpdateRequest;
import com.grup14.luterano.response.alumno.InasistenciaAlumnoResponse;
import com.grup14.luterano.response.alumno.InasistenciaAlumnoResponseList;
import com.grup14.luterano.service.InasistenciaAlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/inasistencia-alumno")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') ")


public class InasistenciaAlumnoController {

    @Autowired
    private InasistenciaAlumnoService inasistenciaAlumnoService;


    @PostMapping("/create")
    @Operation(summary = "Crea un nuevo registro de asistencia del alumno", description = "Crea inasistencia alumno con preceptor,alumno,estado,fecha.")
    public ResponseEntity<InasistenciaAlumnoResponse> createInasistenciaAlumno (@RequestBody @Validated InasistenciaAlumnoRequest inasistenciaAlumnoRequest) {
        try {
            return ResponseEntity.ok(inasistenciaAlumnoService.crearInasistenciaAlumno(inasistenciaAlumnoRequest));
        } catch (AlumnoException e) {
            return ResponseEntity.status(422).body(InasistenciaAlumnoResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaAlumnoResponse.builder().code(-2).mensaje(e.getMessage()).build()); }
    }


    @PutMapping("/update/{id}")
    @Operation(summary = "Actualiza el registro de una inasistencia de alumno", description = "Actualiza una inasistencia alumno por su ID.")
    public ResponseEntity<InasistenciaAlumnoResponse> updateInasistenciaDocente(
            @PathVariable("id") Long id,
            @RequestBody @Validated InasistenciaAlumnoUpdateRequest request) {
        try {
            return ResponseEntity.ok(inasistenciaAlumnoService.updateInasistenciaAlumno(id, request));
        } catch (IllegalArgumentException e) {
            // El ID no es válido o la entidad no existe
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(InasistenciaAlumnoResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            // Otros errores internos
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaAlumnoResponse.builder().code(-2).mensaje("Error interno del servidor").build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un registro de inasistencia de alumno", description = "Elimina una inasistencia por su ID.")
    public ResponseEntity<InasistenciaAlumnoResponse> deleteInasistenciaAlumno(@PathVariable Long id) {
        try {
            inasistenciaAlumnoService.deleteInasistenciaAlumno(id);
            return ResponseEntity.ok(InasistenciaAlumnoResponse.builder()
                    .code(200)
                    .mensaje("Inasistencia eliminada exitosamente.")
                    .build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(InasistenciaAlumnoResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaAlumnoResponse.builder().code(-2).mensaje("Error interno del servidor").build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una inasistencia de Alumno por ID", description = "Devuelve los detalles de una inasistencia específica.")
    public ResponseEntity<InasistenciaAlumnoResponse> getInasistenciaAlumnoById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inasistenciaAlumnoService.getInasistenciaAlumnoById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaAlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todas las inasistencias de los alumnos", description = "Devuelve una lista de todas las inasistencias registradas.")
    public ResponseEntity<InasistenciaAlumnoResponseList> listInasistenciasAlumno() {
        try {
            return ResponseEntity.ok(inasistenciaAlumnoService.listInasistenciaAlumno());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaAlumnoResponseList.builder()
                            .inasistenciaAlumnoDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }


    }


}
