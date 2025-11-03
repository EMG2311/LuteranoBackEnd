package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.PreceptorCursoException;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.service.PreceptorCursoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("preceptorCurso")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@Tag(
        name = "Preceptor Controller",
        description = "Controlador encargado de la gestión de preceptores. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR"
)
@AllArgsConstructor
public class PreceptorCursoController {

    private final PreceptorCursoService preceptorCursoService;

    @PostMapping("/{cursoId}/preceptor/{preceptorId}")
    @Operation(summary = "Asigna un preceptor a un curso")
    public ResponseEntity<CursoResponse> asignarPreceptor(
            @PathVariable Long cursoId,
            @PathVariable Long preceptorId) {
        try {
            return ResponseEntity.ok(preceptorCursoService.asignarPreceptorCurso(preceptorId, cursoId));
        } catch (PreceptorCursoException e) {
            return ResponseEntity.status(422).body(
                    CursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping("/{cursoId}/preceptor")
    @Operation(summary = "Desasigna el preceptor de un curso")
    public ResponseEntity<CursoResponse> desasignarPreceptor(@PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(preceptorCursoService.desasignarPreceptorCurso(cursoId));
        } catch (PreceptorCursoException e) {
            return ResponseEntity.status(422).body(
                    CursoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CursoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }
}
