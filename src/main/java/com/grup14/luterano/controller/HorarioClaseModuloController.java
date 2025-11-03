package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.HorarioClaseModuloException;
import com.grup14.luterano.request.horarioClaseModulo.SlotHorarioRequest;
import com.grup14.luterano.response.horarioClaseModulo.HorarioClaseModuloResponse;
import com.grup14.luterano.service.HorarioClaseModuloService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/horarios")
@RequiredArgsConstructor
public class HorarioClaseModuloController {

    private final HorarioClaseModuloService horarioClaseModuloService;

    @PostMapping("/cursos/{cursoId}/materias/{materiaId}")
    @Operation(summary = "Asigna horarios (día + módulo) a una materia del curso")
    public ResponseEntity<HorarioClaseModuloResponse> asignarHorarios(
            @PathVariable Long cursoId,
            @PathVariable Long materiaId,
            @RequestBody List<SlotHorarioRequest> slots) {
        try {
            var resp = horarioClaseModuloService.asignarHorariosAMateriaDeCurso(cursoId, materiaId, slots);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (HorarioClaseModuloException e) {
            return ResponseEntity.status(422).body(
                    HorarioClaseModuloResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .horarioClaseModuloDto(null)
                            .slotsModificados(Collections.emptyList())
                            .slotsConConflicto(Collections.emptyList())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    HorarioClaseModuloResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .horarioClaseModuloDto(null)
                            .slotsModificados(Collections.emptyList())
                            .slotsConConflicto(Collections.emptyList())
                            .build()
            );
        }
    }

    @DeleteMapping("/cursos/{cursoId}/materias/{materiaId}")
    @Operation(summary = "Desasigna horarios (día + módulo) de una materia del curso")
    public ResponseEntity<HorarioClaseModuloResponse> desasignarHorarios(
            @PathVariable Long cursoId,
            @PathVariable Long materiaId,
            @RequestBody List<SlotHorarioRequest> slots) {
        try {
            var resp = horarioClaseModuloService.desasignarHorariosAMateriaDeCurso(
                    cursoId, materiaId, slots);
            return ResponseEntity.ok(resp);
        } catch (HorarioClaseModuloException e) {
            return ResponseEntity.status(422).body(
                    HorarioClaseModuloResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .horarioClaseModuloDto(null)
                            .slotsModificados(Collections.emptyList())
                            .slotsConConflicto(Collections.emptyList())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    HorarioClaseModuloResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .horarioClaseModuloDto(null)
                            .slotsModificados(Collections.emptyList())
                            .slotsConConflicto(Collections.emptyList())
                            .build()
            );
        }
    }
}
