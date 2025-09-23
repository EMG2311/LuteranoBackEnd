package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.MateriaCursoException;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.preceptorCurso.PreceptorCursoResponse;
import com.grup14.luterano.service.PreceptorCursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/preceptorCurso")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@Tag(
        name = "PreceptorCurso Controller",
        description = "Controlador encargado de la gestión de relacion PreceptorCurso. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR"
)
public class PreceptorCursoController {

    private final PreceptorCursoService preceptorCursoService;

    public  PreceptorCursoController(PreceptorCursoService preceptorCursoService){
        this.preceptorCursoService=preceptorCursoService;
    }


    @PostMapping("/asignarPreceptor/{preceptorId}/{cursoId}")
    @Operation(summary = "Asignar un preceptor a un curso")
    public ResponseEntity<PreceptorCursoResponse> asignarTutorAAlumno(
            @PathVariable Long preceptorId,
            @PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(preceptorCursoService.asignarPreceptorACruso(preceptorId, cursoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    PreceptorCursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    PreceptorCursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }
    @PostMapping("/desasignarPreceptor/{preceptorId}/{cursoId}")
    @Operation(summary = "Desasigna un preceptor de un curso")
    public ResponseEntity<PreceptorCursoResponse> desasignarDocente(
            @PathVariable Long preceptorId,
            @PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(preceptorCursoService.desasignarPreceptorACurso(preceptorId, cursoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    PreceptorCursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    PreceptorCursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }
}
