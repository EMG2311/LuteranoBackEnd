package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.request.docente.DocenteRequest;
import com.grup14.luterano.request.docente.DocenteUpdateRequest;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.response.docente.DocenteResponseList;
import com.grup14.luterano.service.DocenteService;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/docente")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@CrossOrigin(origins = "*")
@Tag(
        name = "Docente Controller",
        description = "Controlador encargado de la gestión a los usuarios. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR"
)
public class DocenteController {

    private final DocenteService docenteService;

    public DocenteController(DocenteService docenteService){
        this.docenteService=docenteService;
    }


    @PostMapping("/create")
    @Operation(summary = "Crea un docente", description = "Crea un docente, debe existir un usuario con ese mail previamente")
    public ResponseEntity<DocenteResponse> createDocente(
            @RequestBody @Validated({Default.class, MayorDeEdadGruoup.class}) DocenteRequest docenteRequest) {
        try {
            return ResponseEntity.ok(docenteService.crearDocente(docenteRequest));
        } catch (DocenteException d) {
            return ResponseEntity.status(422).body(docenteRequest.toResponse(d.getMessage(), -1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(docenteRequest.toResponse("Error no controlado " + e.getMessage(), -2));
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza un docente", description = "Actualiza un docente con los datos que se envíen")
    public ResponseEntity<DocenteResponse> updateDocente(
            @RequestBody  @Validated({Default.class, MayorDeEdadGruoup.class}) DocenteUpdateRequest updateRequest) {
        try {
            return ResponseEntity.ok(docenteService.updateDocente(updateRequest));
        } catch (DocenteException d) {
            return ResponseEntity.status(422).body(DocenteResponse.builder()
                    .code(-1)
                    .mensaje(d.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DocenteResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un docente por id", description = "Elimina un docente si existe")
    public ResponseEntity<DocenteResponse> deleteDocente(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(docenteService.deleteDocente(id));
        } catch (DocenteException d) {
            return ResponseEntity.status(422).body(DocenteResponse.builder()
                    .code(-1)
                    .mensaje(d.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DocenteResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todos los docentes")
    public ResponseEntity<DocenteResponseList> listDocentes() {
        try {
            return ResponseEntity.ok(docenteService.listDocentes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DocenteResponseList.builder()
                            .docenteDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/{docenteId}/materias")
    @Operation(summary = "Asigna una lista de materias a un docente")
    public ResponseEntity<DocenteResponse> asignarMaterias(
            @PathVariable Long docenteId,
            @RequestBody List<Long> materiaIds) {
        try {
            DocenteResponse response = docenteService.asignarMaterias(docenteId, materiaIds);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DocenteResponse.builder()
                            .docente(null)
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (DocenteException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(DocenteResponse.builder()
                            .docente(null)
                            .code(-3)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DocenteResponse.builder()
                            .docente(null)
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{docenteId}/materias")
    @Operation(summary = "Desasigna múltiples materias de un docente")
    public ResponseEntity<DocenteResponse> desasignarMaterias(
            @PathVariable Long docenteId,
            @RequestBody List<Long> materiasIds) {
        try {
            DocenteResponse response = docenteService.desasignarMaterias(docenteId, materiasIds);
            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(DocenteResponse.builder()
                            .docente(null)
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(DocenteResponse.builder()
                            .docente(null)
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }


}
