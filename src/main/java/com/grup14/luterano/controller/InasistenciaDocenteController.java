package com.grup14.luterano.controller;


import com.grup14.luterano.dto.docente.InasistenciaDocenteDto;
import com.grup14.luterano.exeptions.InasistenciaDocenteException;
import com.grup14.luterano.request.docente.InasistenciaDocenteRequest;
import com.grup14.luterano.request.docente.InasistenciaDocenteUpdateRequest;
import com.grup14.luterano.response.docente.InasistenciaDocenteResponse;
import com.grup14.luterano.response.docente.InasistenciaDocenteResponseList;
import com.grup14.luterano.service.InasistenciaDocenteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/inasistencia-docente")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR') ") //or hasRole('DOCENTE') or hasRole('PRECEPTOR') ????


public class InasistenciaDocenteController {

    @Autowired
    private InasistenciaDocenteService inasistenciaDocenteService;

    @PostMapping("/create")
    @Operation(summary = "Crea un nuevo registro inasistencia de docente", description = "Crea inasistencia docente con preceptor,docente,estado,fecha.")
    public ResponseEntity<InasistenciaDocenteResponse> createInasistenciaDocente (@RequestBody @Validated InasistenciaDocenteRequest inasistenciaDocenteRequest) {
        try {
            return ResponseEntity.ok(inasistenciaDocenteService.crearInasistenciaDocente(inasistenciaDocenteRequest));
        } catch (InasistenciaDocenteException e) {
            return ResponseEntity.status(422).body(InasistenciaDocenteResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaDocenteResponse.builder().code(-2).mensaje(e.getMessage()).build()); }
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Actualiza el registro de una inasistencia de docente", description = "Actualiza una inasistencia docente por su ID.")
    public ResponseEntity<InasistenciaDocenteResponse> updateInasistenciaDocente(
            @PathVariable("id") Long id,
            @RequestBody @Validated InasistenciaDocenteUpdateRequest request) {
        try {
            return ResponseEntity.ok(inasistenciaDocenteService.updateInasistenciaDocente(id, request));
        } catch (InasistenciaDocenteException e) {
            // El ID no es válido o la entidad no existe
            return ResponseEntity.status(422)
                    .body(InasistenciaDocenteResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            // Otros errores internos
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaDocenteResponse.builder().code(-2).mensaje("Error interno del servidor").build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un registro de inasistencia de docente", description = "Elimina una inasistencia por su ID.")
    public ResponseEntity<InasistenciaDocenteResponse> deleteInasistenciaDocente(@PathVariable Long id) {
        try {
            inasistenciaDocenteService.deleteInasistenciaDocente(id);
            return ResponseEntity.ok(InasistenciaDocenteResponse.builder()
                    .code(200)
                    .mensaje("Inasistencia eliminada exitosamente.")
                    .build());
        } catch (InasistenciaDocenteException e) {
            return ResponseEntity.status(422)
                    .body(InasistenciaDocenteResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaDocenteResponse.builder().code(-2).mensaje("Error interno del servidor").build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene una inasistencia de docente por ID", description = "Devuelve los detalles de una inasistencia específica.")
    public ResponseEntity<InasistenciaDocenteResponse> getInasistenciaDocenteById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(inasistenciaDocenteService.getInasistenciaDocenteById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaDocenteResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todas las inasistencias de docentes", description = "Devuelve una lista de todas las inasistencias registradas.")
    public ResponseEntity<InasistenciaDocenteResponseList> listInasistenciasDocente() {
        try {
            return ResponseEntity.ok(inasistenciaDocenteService.listInasistenciasDocente());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InasistenciaDocenteResponseList.builder()
                            .inasistenciaDocenteDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }



    }

}



