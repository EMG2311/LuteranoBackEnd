package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.CursoException;
import com.grup14.luterano.request.curso.CursoRequest;
import com.grup14.luterano.request.curso.CursoUpdateRequest;
import com.grup14.luterano.response.curso.CursoResponse;
import com.grup14.luterano.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/curso")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')") //or hasRole('DOCENTE') or hasRole('PRECEPTOR') ????
@CrossOrigin(origins = "*")


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
                    .body(cursoRequest.toResponse(e.getMessage(),-2));
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


}
