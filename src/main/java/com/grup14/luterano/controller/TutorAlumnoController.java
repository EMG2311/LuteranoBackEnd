package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.MateriaCursoException;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.service.TutorAlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tutorAlumno")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@Tag(
        name = "TutorAlumno Controller",
        description = "Controlador encargado de la gestión de la relación Tutor-Alumno"
)
public class TutorAlumnoController {
    private final TutorAlumnoService tutorAlumnoService;

    public TutorAlumnoController(TutorAlumnoService tutorAlumnoService) {
        this.tutorAlumnoService = tutorAlumnoService;
    }

    @PostMapping("/asignarTutor/{tutorId}/{alumnoId}")
    @Operation(summary = "Asignar un tutor de un alumno")
    public ResponseEntity<AlumnoResponse> asignarTutorAAlumno(
            @PathVariable Long tutorId,
            @PathVariable Long alumnoId) {
        try {
            return ResponseEntity.ok(tutorAlumnoService.asignarTutorAAlumno(tutorId, alumnoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    AlumnoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/desasignarTutor/{tutorId}/{alumnoId}")
    @Operation(summary = "Desasigna un tutor de un alumno")
    public ResponseEntity<AlumnoResponse> desasignarDocente(
            @PathVariable Long tutorId,
            @PathVariable Long alumnoId) {
        try {
            return ResponseEntity.ok(tutorAlumnoService.desasignarTutorAlumno(tutorId, alumnoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    AlumnoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{tutorId}/alumnos")
    @Operation(summary = "Lista los alumnos a cargo del tutor")
    public ResponseEntity<AlumnoResponseList> listarAlumnosACargo(
            @PathVariable Long tutorId) {
        try {
            return ResponseEntity.ok(tutorAlumnoService.listarAlumnosACargo(tutorId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    AlumnoResponseList.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    AlumnoResponseList.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }
}
