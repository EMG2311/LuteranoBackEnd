package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.MateriaCursoException;
import com.grup14.luterano.response.MateriaCurso.MateriaCursoListResponse;
import com.grup14.luterano.response.MateriaCurso.MateriaCursoResponse;
import com.grup14.luterano.service.MateriaCursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materiasCurso")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@Tag(
        name = "MateriaCurso Controller",
        description = "Controlador encargado de la gestión de la relación Materia-Curso y asignación de docentes."
)
public class MateriaCursoController {

    private final MateriaCursoService materiaCursoService;

    public MateriaCursoController(MateriaCursoService materiaCursoService) {
        this.materiaCursoService = materiaCursoService;
    }

    @PostMapping("/asignarMaterias/{cursoId}")
    @Operation(summary = "Asigna una materia a un curso")
    public ResponseEntity<MateriaCursoResponse> asignarMaterias(
            @PathVariable Long cursoId,
            @RequestBody List<Long> materiaIds) {
        try {
            return ResponseEntity.ok(materiaCursoService.asignarMateriasACurso(materiaIds, cursoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    MateriaCursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    MateriaCursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/quitarMaterias/{cursoId}")
    @Operation(summary = "Quita varias materias de un curso")
    public ResponseEntity<MateriaCursoListResponse> quitarMaterias(
            @PathVariable Long cursoId,
            @RequestBody List<Long> materiaIds) {
        try {
            return ResponseEntity.ok(materiaCursoService.quitarMateriasDeCurso(materiaIds, cursoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    MateriaCursoListResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    MateriaCursoListResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }


    @GetMapping("/listarMateriasDeCurso/{cursoId}")
    @Operation(summary = "Lista todas las materias de un curso")
    public ResponseEntity<MateriaCursoListResponse> listarMateriasDeCurso(
            @PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(materiaCursoService.listarMateriasDeCurso(cursoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    MateriaCursoListResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .materiaCursoDtoLis(List.of())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    MateriaCursoListResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .materiaCursoDtoLis(List.of())
                            .build());
        }
    }

    @GetMapping("/listarCursosDeMateria/{materiaId}")
    @Operation(summary = "Lista todos los cursos de una materia")
    public ResponseEntity<MateriaCursoListResponse> listarCursosDeMateria(
            @PathVariable Long materiaId) {
        try {
            return ResponseEntity.ok(materiaCursoService.listarCursosDeMateria(materiaId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    MateriaCursoListResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .materiaCursoDtoLis(List.of())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    MateriaCursoListResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .materiaCursoDtoLis(List.of())
                            .build());
        }
    }

    @PostMapping("/asignarDocente/{materiaId}/{cursoId}/{docenteId}")
    @Operation(summary = "Asigna un docente a una materia de un curso")
    public ResponseEntity<MateriaCursoResponse> asignarDocente(
            @PathVariable Long materiaId,
            @PathVariable Long cursoId,
            @PathVariable Long docenteId) {
        try {
            return ResponseEntity.ok(materiaCursoService.asignarDocente(materiaId, cursoId, docenteId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    MateriaCursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    MateriaCursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/desasignarDocente/{materiaId}/{cursoId}")
    @Operation(summary = "Desasigna un docente de una materia de un curso")
    public ResponseEntity<MateriaCursoResponse> desasignarDocente(
            @PathVariable Long materiaId,
            @PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(materiaCursoService.desasignarDocente(materiaId, cursoId));
        } catch (MateriaCursoException e) {
            return ResponseEntity.status(422).body(
                    MateriaCursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    MateriaCursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }
}
