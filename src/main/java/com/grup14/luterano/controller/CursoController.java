package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.CursoException;
import com.grup14.luterano.exeptions.PreceptorCursoException;
import com.grup14.luterano.request.curso.CursoRequest;
import com.grup14.luterano.request.curso.CursoUpdateRequest;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.response.curso.CursoResponseList;
import com.grup14.luterano.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/curso")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
public class CursoController {

    @Autowired
    private CursoService cursoService;


    @PostMapping("/create")
    @Operation(summary = "Crea un nuevo curso", description = "Requiere que el usuario tenga un rol de ADMIN O DIRECTOR ")
    public ResponseEntity<CursoResponse> createCurso(@RequestBody @Validated CursoRequest cursoRequest) {
        try {
            return ResponseEntity.ok(cursoService.crearCurso(cursoRequest));
        } catch (CursoException e) {
            return ResponseEntity.status(422).body(CursoResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CursoResponse.builder().code(-2).mensaje("Error no controlado: " + e.getMessage()).build());
        }
    }


    @PutMapping("/update")
    @Operation(summary = "Actualiza un curso", description = "Actualiza un curso existente por su ID.")
    public ResponseEntity<CursoResponse> updateCurso(@RequestBody @Validated({Default.class}) CursoUpdateRequest updateRequest) {
        try {
            return ResponseEntity.ok(cursoService.updateCurso(updateRequest));
        } catch (CursoException e) {
            return ResponseEntity.status(422).body(CursoResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un curso por id", description = "Elimina un curso si existe.")
    public ResponseEntity<CursoResponse> deleteCurso(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cursoService.deleteCurso(id));
        } catch (CursoException e) {
            return ResponseEntity.status(422).body(CursoResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CursoResponse.builder()
                            .code(-2).mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('DOCENTE')")
    @Operation(summary = "Lista todos los cursos", description = "Devuelve una lista de todos los cursos.")
    public ResponseEntity<CursoResponseList> listCursos() {
        try {
            return ResponseEntity.ok(cursoService.listCursos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CursoResponseList.builder()
                            .cursoDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un curso por ID", description = "Devuelve un curso específico por su ID.")
    public ResponseEntity<CursoResponse> getCursoById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cursoService.getCursoById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }

    }

    @GetMapping("/list/preceptor/{preceptorId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
    @Operation(summary = "lista los cursos de un preceptor")
    public ResponseEntity<CursoResponseList> listarCursosPorPreceptor(@PathVariable Long preceptorId) {
        try {
            return ResponseEntity.ok(cursoService.listarCursosPorPreceptor(preceptorId));
        } catch (PreceptorCursoException e) {
            return ResponseEntity.status(422).body(
                    CursoResponseList.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CursoResponseList.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/list/docente/{docenteId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('DOCENTE')")
    @Operation(summary = "lista los cursos de un docente")
    public ResponseEntity<CursoResponseList> listarCursosPorDocente(@PathVariable Long docenteId) {
        try {
            return ResponseEntity.ok(cursoService.listarCursosPorDocente(docenteId));
        } catch (PreceptorCursoException e) {
            return ResponseEntity.status(422).body(
                    CursoResponseList.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CursoResponseList.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/intercambiar-aulas")
    @Operation(summary = "Intercambia las aulas entre dos cursos", description = "Recibe los IDs de dos cursos y les intercambia el aula asignada.")
    public ResponseEntity<CursoResponse> intercambiarAulas(@RequestBody @Validated com.grup14.luterano.request.curso.IntercambiarAulasRequest req) {
        try {
            return ResponseEntity.ok(cursoService.intercambiarAulas(req));
        } catch (CursoException e) {
            return ResponseEntity.status(422).body(CursoResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CursoResponse.builder().code(-2).mensaje("Error no controlado: " + e.getMessage()).build());
        }
    }


}
