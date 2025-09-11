package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.TutorException;
import com.grup14.luterano.request.tutor.TutorRequest;
import com.grup14.luterano.request.tutor.TutorUpdateRequest;
import com.grup14.luterano.response.tutor.TutorResponse;
import com.grup14.luterano.response.tutor.TutorResponseList;
import com.grup14.luterano.service.TutorService;
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
@RequestMapping("/tutor")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
@Tag(
        name = "Tutor Controller",
        description = "Controlador encargado de la gestión de tutores. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR"
)
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @PostMapping("/create")
    @Operation(summary = "Crea un tutor")
    public ResponseEntity<TutorResponse> createTutor(
            @RequestBody @Validated({Default.class, MayorDeEdadGruoup.class}) TutorRequest tutorRequest) {
        try {
            return ResponseEntity.ok(tutorService.crearTutor(tutorRequest));
        } catch (TutorException e) {
            return ResponseEntity.status(422).body(TutorResponse.builder()
                    .code(-1)
                    .mensaje("Error al crear tutor "+e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TutorResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado "+e.getMessage())
                            .build());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza un tutor")
    public ResponseEntity<TutorResponse> updateTutor(
            @RequestBody @Validated({Default.class, MayorDeEdadGruoup.class}) TutorUpdateRequest updateRequest) {
        try {
            return ResponseEntity.ok(tutorService.updateTutor(updateRequest));
        } catch (TutorException e) {
            return ResponseEntity.status(422).body(TutorResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TutorResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un tutor")
    public ResponseEntity<TutorResponse> deleteTutor(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(tutorService.deleteTutor(id));
        } catch (TutorException e) {
            return ResponseEntity.status(422).body(TutorResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TutorResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todos los tutores")
    public ResponseEntity<TutorResponseList> listTutores() {
        try {
            return ResponseEntity.ok(tutorService.listTutores());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(TutorResponseList.builder()
                            .tutores(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }
}
