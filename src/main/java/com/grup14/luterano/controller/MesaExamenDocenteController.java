package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.MesaExamenException;
import com.grup14.luterano.request.mesaExamenDocente.AsignarDocentesRequest;
import com.grup14.luterano.response.mesaExamenDocente.DocentesDisponiblesResponse;
import com.grup14.luterano.response.mesaExamenDocente.MesaExamenDocentesResponse;
import com.grup14.luterano.service.MesaExamenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mesa-examen/{mesaExamenId}/docentes")
@RequiredArgsConstructor
    public class MesaExamenDocenteController {

    private final MesaExamenService mesaExamenService;

    @GetMapping("/disponibles")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR')")
    public ResponseEntity<DocentesDisponiblesResponse> listarDocentesDisponibles(
            @PathVariable Long mesaExamenId) {
        try {
            DocentesDisponiblesResponse response = mesaExamenService.listarDocentesDisponibles(mesaExamenId);
            return ResponseEntity.ok(response);
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(DocentesDisponiblesResponse.builder()
                    .mensaje(e.getMessage())
                    .code(-1)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(DocentesDisponiblesResponse.builder()
                    .mensaje("Error no controlado")
                    .code(-2)
                    .build());
        }
    }

    @PostMapping("/asignar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR')")
    public ResponseEntity<MesaExamenDocentesResponse> asignarDocentes(
            @PathVariable Long mesaExamenId,
            @Valid @RequestBody AsignarDocentesRequest request) {
        try {
            MesaExamenDocentesResponse response = mesaExamenService.asignarDocentes(mesaExamenId, request);
            return ResponseEntity.ok(response);
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenDocentesResponse.builder()
                    .mensaje(e.getMessage())
                    .code(-1)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(MesaExamenDocentesResponse.builder()
                    .mensaje("Error no controlado")
                    .code(-2)
                    .build());
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<MesaExamenDocentesResponse> listarDocentesAsignados(
            @PathVariable Long mesaExamenId) {
        try {
            MesaExamenDocentesResponse response = mesaExamenService.listarDocentesAsignados(mesaExamenId);
            return ResponseEntity.ok(response);
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenDocentesResponse.builder()
                    .mensaje(e.getMessage())
                    .code(-1)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(MesaExamenDocentesResponse.builder()
                    .mensaje("Error no controlado")
                    .code(-2)
                    .build());
        }
    }

    @PutMapping("/modificar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR')")
    public ResponseEntity<MesaExamenDocentesResponse> modificarDocente(
            @PathVariable Long mesaExamenId,
            @RequestParam Long docenteActualId,
            @RequestParam Long nuevoDocenteId) {
        try {
            MesaExamenDocentesResponse response = mesaExamenService.modificarDocente(
                    mesaExamenId, docenteActualId, nuevoDocenteId);
            return ResponseEntity.ok(response);
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenDocentesResponse.builder()
                    .mensaje(e.getMessage())
                    .code(-1)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(MesaExamenDocentesResponse.builder()
                    .mensaje("Error no controlado")
                    .code(-2)
                    .build());
        }
    }
}