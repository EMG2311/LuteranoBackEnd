package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.exeptions.DocenteException;
import com.grup14.luterano.request.alumno.AlumnoFiltrosRequest;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@RequestMapping("/alumno")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')") //or hasRole('DOCENTE') or hasRole('PRECEPTOR') ????
@Tag(
        name = "Alumno Controller",
        description = "Controlador encargado de la gestión de alumnos. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR o PRECEPTOR."
)
public class AlumnoController {

    private final AlumnoService alumnoService;

    public AlumnoController(AlumnoService alumnoService){
        this.alumnoService=alumnoService;
    }

    @PostMapping("/create")
    @Operation(summary = "Crea un nuevo alumno",
            description = "Requiere que el usuario tenga un rol de ADMIN O DIRECTOR o PRECEPTOR")
    public ResponseEntity<AlumnoResponse> createAlumno(@RequestBody @Validated AlumnoRequest alumnoRequest) {
        try {
            return ResponseEntity.ok(alumnoService.crearAlumno(alumnoRequest));
        } catch (AlumnoException e) {
            return ResponseEntity.status(422).body(
                            alumnoRequest.toResponse(e.getMessage(),-1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(alumnoRequest.toResponse(e.getMessage(),-2));
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
    @PostMapping("/filtros")
    @Operation(summary = "Lista alumnos con filtros dinámicos",
            description = "Permite filtrar por nombre, apellido, dni, año y división")
    public ResponseEntity<AlumnoResponseList> listarAlumnos(@RequestBody @Validated AlumnoFiltrosRequest filtros) {
        try {
            return ResponseEntity.ok(alumnoService.listAlumnos(filtros));
        } catch (AlumnoException e) {
            // error controlado (-1)
            return ResponseEntity.status(422).body(
                    AlumnoResponseList.builder()
                            .alumnoDtos(List.of())
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            // error no controlado (-2)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponseList.builder()
                            .alumnoDtos(List.of())
                            .code(-2)
                            .mensaje(e.getMessage())
                            .build()
            );
        }
    }


}
