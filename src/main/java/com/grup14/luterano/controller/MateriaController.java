package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.MateriaException;
import com.grup14.luterano.request.materia.MateriaRequest;
import com.grup14.luterano.request.materia.MateriaUpdateRequest;
import com.grup14.luterano.response.Materia.MateriaResponse;
import com.grup14.luterano.response.Materia.MateriaResponseList;
import com.grup14.luterano.service.MateriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/materias")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@CrossOrigin(origins = "*")
@Tag(
        name = "Materia Controller",
        description = "Controlador encargado de la gestión de las materias. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR."
)
public class MateriaController {

    private final MateriaService materiaService;

    public MateriaController(MateriaService materiaService){
        this.materiaService=materiaService;
    }

    @PostMapping("/create")
    @Operation(summary = "Crea una materia", description = "En este metodo se crea la materia, pero no se debe pasar el curso" +
            "ya que no hace nada en este paso")
    public ResponseEntity<MateriaResponse> createMateria(
            @RequestBody @Validated MateriaRequest materiaRequest) {
        try {
            return ResponseEntity.ok(materiaService.crearMateria(materiaRequest));
        } catch (MateriaException e) {
            return ResponseEntity.status(422).body(
                    MateriaResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MateriaResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza una materia")
    public ResponseEntity<MateriaResponse> updateMateria(
            @RequestBody @Validated MateriaUpdateRequest updateRequest) {
        try {
            return ResponseEntity.ok(materiaService.updateMateria(updateRequest));
        } catch (MateriaException e) {
            return ResponseEntity.status(422).body(
                    MateriaResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MateriaResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todas las materias")
    public ResponseEntity<MateriaResponseList> listarMaterias() {
        try {
            return ResponseEntity.ok(materiaService.listarMaterias());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MateriaResponseList.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina una materia por id")
    public ResponseEntity<MateriaResponse> borrarMateria(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(materiaService.borrarMateria(id));
        } catch (MateriaException e) {
            return ResponseEntity.status(422).body(
                    MateriaResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(MateriaResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }
}
