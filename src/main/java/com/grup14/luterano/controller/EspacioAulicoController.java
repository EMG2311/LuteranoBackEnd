package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.EspacioAulicoException;
import com.grup14.luterano.request.espacioAulico.EspacioAulicoRequest;
import com.grup14.luterano.request.espacioAulico.EspacioAulicoUpdateRequest;
import com.grup14.luterano.response.espacioAulico.EspacioAulicoResponse;
import com.grup14.luterano.response.espacioAulico.EspacioAulicoResponseList;
import com.grup14.luterano.service.EspacioAulicoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/espacio-aulico")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('AUXILIAR')")

public class EspacioAulicoController {

    private final EspacioAulicoService espacioAulicoService;

    public EspacioAulicoController(EspacioAulicoService espacioAulicoService) {
        this.espacioAulicoService = espacioAulicoService;
    }


    @PostMapping("/create")
    @Operation(summary = "Crea un espacio aulico", description = "Solo roles ADMIN/DIRECTOR/AUXILIAR. Requiere nombre, ubicación y capacidad.")
    public ResponseEntity<EspacioAulicoResponse> crearEspacioAulico(
            @Valid @RequestBody EspacioAulicoRequest request) {
        try {
            return ResponseEntity.ok(espacioAulicoService.crearEspacioAulico(request));
        } catch (EspacioAulicoException e) {
            return ResponseEntity.status(422).body(EspacioAulicoResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(EspacioAulicoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }


    @PutMapping("/update")
    @Operation(summary = "Actualiza un espacio áulico",
            description = "Modifica los datos de un espacio existente.")
    public ResponseEntity<EspacioAulicoResponse> updateEspacio(
            @RequestBody @Validated({Default.class}) EspacioAulicoUpdateRequest request) {
        try {
            EspacioAulicoResponse response = espacioAulicoService.updateEspacioAulico(request);
            return ResponseEntity.ok(response);
        } catch (EspacioAulicoException e) {
            return ResponseEntity.status(422).body(EspacioAulicoResponse.builder()
                    .code(-1)
                    .mensaje("Error al crear espacio aulico " + e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EspacioAulicoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }

    }


    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un espacio áulico por id", description = "Elimina un espacio si existe.")
    public ResponseEntity<EspacioAulicoResponse> deleteEspacio(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(espacioAulicoService.deleteEspacioAulico(id));
        } catch (EspacioAulicoException e) {
            return ResponseEntity.status(422).body(EspacioAulicoResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EspacioAulicoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }

    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('DOCENTE') or hasRole('PRECEPTOR') or hasRole('AUXILIAR')")
    @Operation(summary = "Lista todos los espacios áulicos", description = "Obtiene una lista de todos los espacios áulicos disponibles.")
    public ResponseEntity<EspacioAulicoResponseList> listEspacios() {
        try {
            return ResponseEntity.ok(espacioAulicoService.listEspacioAulico());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(EspacioAulicoResponseList.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

}


