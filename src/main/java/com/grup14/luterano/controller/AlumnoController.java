package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.request.docente.DocenteUpdateRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.response.docente.DocenteResponse;
import com.grup14.luterano.response.docente.DocenteResponseList;
import com.grup14.luterano.service.AlumnoService;
import com.grup14.luterano.validation.MayorDeEdadGruoup;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/alumno")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') o or hasRole('PRECEPTOR')") //or hasRole('DOCENTE') or hasRole('PRECEPTOR') ????
@CrossOrigin(origins = "*")


public class AlumnoController {
    @Autowired
    private AlumnoService alumnoService;

    @PostMapping("/create")
    @Operation(summary = "Crea un nuevo alumno",
            description = "Requiere que el usuario tenga un rol de ADMIN O DIRECTOR ")
    public ResponseEntity<AlumnoResponse> createAlumno(@RequestBody @Validated AlumnoRequest alumnoRequest) {
        try {
            return ResponseEntity.ok(alumnoService.crearAlumno(alumnoRequest));
        } catch (AlumnoException e) {  ///  que es la e???? nombre la variable de excepcion
            return ResponseEntity.status(422).body(
                    AlumnoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " )
                            .build());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualiza un alumno",
            description = "Actualiza un alumno con los datos que se envíen.")
    public ResponseEntity<AlumnoResponse> updateAlumno(
            @RequestBody @Validated({Default.class}) AlumnoUpdateRequest updateRequest) {
        try {
            // Llama al servicio para actualizar el alumno
            AlumnoResponse response = alumnoService.updateAlumno(updateRequest);
            return ResponseEntity.ok(response);
        } catch (AlumnoException d) {
            return ResponseEntity.status(422).body(AlumnoResponse.builder()
                    .code(-1)
                    .mensaje(d.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Elimina un alumno por ID",
            description = "Elimina un alumno si existe. Requiere que el usuario tenga rol de ADMIN o DIRECTOR.")
    public ResponseEntity<AlumnoResponse> deleteAlumno(@PathVariable Long id) {
        try {
            // Llama al servicio para eliminar el alumno por su ID
            AlumnoResponse response = alumnoService.deleteAlumno(id);
            return ResponseEntity.ok(response);
        }  catch (AlumnoException d) {
            return ResponseEntity.status(422).body(AlumnoResponse.builder()
                    .code(-1)
                    .mensaje(d.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todos los docentes")
    public ResponseEntity<AlumnoResponseList> listAlumnos() {
        try {
            return ResponseEntity.ok(alumnoService.listAlumnos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AlumnoResponseList.builder()
                            .alumnoDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }


}
