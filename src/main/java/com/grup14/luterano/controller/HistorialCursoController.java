package com.grup14.luterano.controller;

import com.grup14.luterano.dto.HistorialCursoDto;
import com.grup14.luterano.exeptions.HistorialCursoException;
import com.grup14.luterano.repository.HistorialCursoRepository;
import com.grup14.luterano.request.historialCursoRequest.HistorialCursoRequest;
import com.grup14.luterano.response.CursoAlumno.CursoAlumnosResponse;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponse;
import com.grup14.luterano.response.historialCurso.HistorialCursoResponseList;
import com.grup14.luterano.service.HistorialCursoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/historial-curso")

public class HistorialCursoController {

    private final HistorialCursoService historialCursoService;

    public HistorialCursoController(HistorialCursoService historialCursoService){
        this.historialCursoService=historialCursoService;
    }

    @GetMapping("/alumno/{alumnoId}/historial-completo")
    @Operation(
            summary = "Lista todo el historial de un alumno",
            description = "Devuelve todos los cursos por los que pasó un alumno, en todos los ciclos lectivos."
    )
    public ResponseEntity<HistorialCursoResponseList> listarHistorialAlumno(
            @PathVariable Long alumnoId,
            @RequestParam(required = false) Long cicloLectivoId,
            @RequestParam(required = false) Long cursoId) {
        try {
            HistorialCursoResponseList response = historialCursoService.listarHistorialAlumnoFiltrado(alumnoId, cicloLectivoId, cursoId);
            return ResponseEntity.ok(response);

        } catch (HistorialCursoException e) {
            return ResponseEntity.status(422).body(
                    HistorialCursoResponseList.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    HistorialCursoResponseList.builder()
                            .code(-2)
                            .mensaje("Error inesperado: " + e.getMessage())
                            .build()
            );
        }
    }



    @GetMapping("/alumno/{alumnoId}")
    @Operation(
            summary = "Devuelve el historial Curso abierto",
            description = "Devuelve el historial Curso abierto de un alumno"
    )
    public ResponseEntity<HistorialCursoResponse> listarHistorialAlumno(
            @PathVariable Long alumnoId) {
        try {
            HistorialCursoResponse response = historialCursoService.getHistorialCursoActual(alumnoId);
            return ResponseEntity.ok(response);

        } catch (HistorialCursoException e) {
            return ResponseEntity.status(422).body(
                    HistorialCursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    HistorialCursoResponse.builder()
                            .code(-2)
                            .mensaje("Error inesperado: " + e.getMessage())
                            .build()
            );
        }
    }




    @GetMapping("/{cursoId}/alumnos")
    @Operation(
            summary = "Lista alumnos del curso (según asignación vigente)",
            description = "Devuelve los alumnos con HistorialCurso abierto (fechaHasta NULL) en el ciclo lectivo indicado. " +
                    "Si no se especifica cicloLectivoId, se usa el ciclo activo."
    )
    public ResponseEntity<CursoAlumnosResponse> listarAlumnosPorCurso(
            @PathVariable Long cursoId,
            @RequestParam(value = "cicloLectivoId", required = false) Long cicloLectivoId
    ) {
        try {
            CursoAlumnosResponse response = historialCursoService.listarAlumnosPorCurso(cursoId, cicloLectivoId);
            return ResponseEntity.ok(response);

        } catch (HistorialCursoException e) {
            return ResponseEntity.status(422).body(
                    CursoAlumnosResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CursoAlumnosResponse.builder()
                            .code(-2)
                            .mensaje("Error inesperado: " + e.getMessage())
                            .build()
            );
        }
    }
}

