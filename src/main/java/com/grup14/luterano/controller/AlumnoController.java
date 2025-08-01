package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alumno")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')") //or hasRole('DOCENTE') or hasRole('PRECEPTOR') ????
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
        } catch (AlumnoException e) {  ///  que es la e????
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


}
