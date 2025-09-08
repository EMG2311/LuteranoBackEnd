package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.AulaException;
import com.grup14.luterano.request.aula.AulaRequest;
import com.grup14.luterano.request.aula.AulaUpdateRequest;
import com.grup14.luterano.response.aula.AulaResponse;
import com.grup14.luterano.response.aula.AulaResponseList;
import com.grup14.luterano.service.AulaService;
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
@RequestMapping("/aula")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')") //or hasRole('DOCENTE') or hasRole('PRECEPTOR') ????
@CrossOrigin(origins = "*")

public class AulaController {

    @Autowired
    private AulaService aulaService;


    @PostMapping("/create")
    @Operation(summary = "Crea una nueva aula", description = "Crea un aula con nombre, ubicación y capacidad.")
    public ResponseEntity<AulaResponse> createAula(@RequestBody @Validated AulaRequest aulaRequest) {
        try {
            return ResponseEntity.ok(aulaService.crearAula(aulaRequest));
        } catch (AulaException e) {
            return ResponseEntity.status(422).body(AulaResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(aulaRequest.toResponse(e.getMessage(),-2));        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza un aula", description = "Actualiza un aula existente por su ID.")
    public ResponseEntity<AulaResponse> updateAula(@RequestBody @Validated({Default.class}) AulaUpdateRequest updateRequest) {
        try {
            return ResponseEntity.ok(aulaService.updateAula(updateRequest));
        } catch (AulaException e) {
            return ResponseEntity.status(422).body(AulaResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AulaResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un aula por id", description = "Elimina un aula si existe.")
    public ResponseEntity<AulaResponse> deleteAula(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(aulaService.deleteAula(id));
        } catch (AulaException e) {
            return ResponseEntity.status(422).body(AulaResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AulaResponse.builder()
                            .code(-2).mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todas las aulas", description = "Devuelve una lista de todas las aulas.")
    public ResponseEntity<AulaResponseList> listAulas() {
        try {
            return ResponseEntity.ok(aulaService.listAulas());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AulaResponseList.builder()
                            .aulaDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }


    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un aula por ID", description = "Devuelve un aula específica por su ID.")
    public ResponseEntity<AulaResponse> getAulaById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(aulaService.getAulaById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AulaResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }
}
