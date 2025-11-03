package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.AsistenciaException;
import com.grup14.luterano.response.inasistenciasAlumno.InasistenciasAlumnoResponse;
import com.grup14.luterano.service.InasistenciasAlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inasistencias")
@RequiredArgsConstructor
public class InasistenciasAlumnoController {

    private final InasistenciasAlumnoService inasistenciasService;

    @GetMapping("/alumno/{alumnoId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<InasistenciasAlumnoResponse> listarInasistenciasPorAlumno(
            @PathVariable Long alumnoId) {
        try {
            InasistenciasAlumnoResponse response = inasistenciasService.listarInasistenciasPorAlumno(alumnoId);
            return ResponseEntity.ok(response);
        } catch (AsistenciaException e) {
            return ResponseEntity.status(422).body(InasistenciasAlumnoResponse.builder()
                    .mensaje("No se encontro el alumno con id " + alumnoId)
                    .code(-1)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(InasistenciasAlumnoResponse.builder()
                    .mensaje("Error no controlado")
                    .code(-2)
                    .build());
        }
    }

    @GetMapping("/alumno/dni/{dni}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<InasistenciasAlumnoResponse> listarInasistenciasPorDni(
            @PathVariable String dni) {
        try {
            InasistenciasAlumnoResponse response = inasistenciasService.listarInasistenciasPorDni(dni);
            return ResponseEntity.ok(response);
        } catch (AsistenciaException | IllegalArgumentException e) {
            return ResponseEntity.status(422).body(InasistenciasAlumnoResponse.builder()
                    .mensaje("No se encontro el alumno con DNI " + dni)
                    .code(-1)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(InasistenciasAlumnoResponse.builder()
                    .mensaje("Error no controlado")
                    .code(-2)
                    .build());
        }
    }
}