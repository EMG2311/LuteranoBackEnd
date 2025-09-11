package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.PreceptorException;
import com.grup14.luterano.request.Preceptor.PreceptorRequest;
import com.grup14.luterano.request.Preceptor.PreceptorUpdateRequest;
import com.grup14.luterano.response.Preceptor.PreceptorResponse;
import com.grup14.luterano.response.Preceptor.PreceptorResponseList;
import com.grup14.luterano.service.PreceptorService;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
@RestController
@RequestMapping("/preceptor")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@Tag(
        name = "Preceptor Controller",
        description = "Controlador encargado de la gestión de preceptores. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR"
)
public class PreceptorController {
    private final PreceptorService preceptorService;

    public PreceptorController(PreceptorService preceptorService) {
        this.preceptorService = preceptorService;
    }

    @PostMapping("/create")
    @Operation(summary = "Crea un preceptor")
    public ResponseEntity<PreceptorResponse> createPreceptor(
            @RequestBody @Validated({Default.class, MayorDeEdadGruoup.class}) PreceptorRequest preceptorRequest) {
        try {
            return ResponseEntity.ok(preceptorService.crearPreceptor(preceptorRequest));
        } catch (PreceptorException e) {
            return ResponseEntity.status(422).body(PreceptorResponse.builder()
                    .code(-1)
                    .mensaje("Error al crear preceptor "+e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PreceptorResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado "+e.getMessage())
                            .build());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza un preceptor")
    public ResponseEntity<PreceptorResponse> updatePreceptor(
            @RequestBody @Validated({Default.class, MayorDeEdadGruoup.class}) PreceptorUpdateRequest updateRequest) {
        try {
            return ResponseEntity.ok(preceptorService.updatePreceptor(updateRequest));
        } catch (PreceptorException e) {
            return ResponseEntity.status(422).body(PreceptorResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PreceptorResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un preceptor")
    public ResponseEntity<PreceptorResponse> deletePreceptor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(preceptorService.deletePreceptor(id));
        } catch (PreceptorException e) {
            return ResponseEntity.status(422).body(PreceptorResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PreceptorResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todos los preceptores")
    public ResponseEntity<PreceptorResponseList> listPreceptores() {
        try {
            return ResponseEntity.ok(preceptorService.listPreceptores());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PreceptorResponseList.builder()
                            .preceptores(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }
}
