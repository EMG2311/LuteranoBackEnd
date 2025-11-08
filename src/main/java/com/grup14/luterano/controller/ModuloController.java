package com.grup14.luterano.controller;

import com.grup14.luterano.dto.modulo.ModuloDto;
import com.grup14.luterano.entities.enums.DiaSemana;
import com.grup14.luterano.exeptions.ModuloException;
import com.grup14.luterano.response.modulo.*;
import com.grup14.luterano.service.ModuloService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/modulos")
@PreAuthorize("hasRole('ADMIN')")
@Tag(
        name = "Módulos Controller",
        description = "Controlador encargado de la gestión de módulos horarios. " +
                "Acceso restringido exclusivamente a usuarios con rol ADMIN."
)
@RequiredArgsConstructor
public class ModuloController {


    private final ModuloService moduloService;

    @GetMapping("curso/{cursoId}/libres")
    @Operation(summary = "Devuelve los módulos libres del curso en un día")
    public ResponseEntity<ModuloListResponse> modulosLibresPorDia(
            @PathVariable Long cursoId,
            @RequestParam DiaSemana dia) {
        try {
            return ResponseEntity.ok(moduloService.modulosLibresDelCursoPorDia(cursoId, dia));
        } catch (ModuloException e) {
            return ResponseEntity.status(422).body(
                    ModuloListResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .modulos(Collections.emptyList())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ModuloListResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .modulos(Collections.emptyList())
                            .build()
            );
        }
    }

    @GetMapping("/curso/{cursoId}/libres/semana")
    @Operation(summary = "Devuelve los módulos libres del curso para toda la semana")
    public ResponseEntity<ModuloSemanaResponse> modulosLibresSemana(@PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(moduloService.modulosLibresDelCursoTodaLaSemana(cursoId));
        } catch (ModuloException e) {
            Map<DiaSemana, List<ModuloDto>> vacio = new EnumMap<>(DiaSemana.class);
            for (DiaSemana d : DiaSemana.values()) vacio.put(d, Collections.emptyList());
            return ResponseEntity.status(422).body(
                    ModuloSemanaResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .modulosPorDia(vacio)
                            .build()
            );
        } catch (Exception e) {
            Map<DiaSemana, List<ModuloDto>> vacio = new EnumMap<>(DiaSemana.class);
            for (DiaSemana d : DiaSemana.values()) vacio.put(d, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ModuloSemanaResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .modulosPorDia(vacio)
                            .build()
            );
        }
    }

    @GetMapping
    @Operation(summary = "Devuelve todos los módulos del colegio (sin estado)")
    public ResponseEntity<ModuloListResponse> todos() {
        try {
            return ResponseEntity.ok(moduloService.todosLosModulos());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ModuloListResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .modulos(java.util.Collections.emptyList())
                            .build()
            );
        }
    }

    // 2) Módulos con estado para un curso y un día
    @GetMapping("/curso/{cursoId}/estado")
    @Operation(summary = "Devuelve los módulos del colegio con su estado (ocupado/libre) para un curso y día")
    public ResponseEntity<ModuloEstadoListResponse> estadoPorDia(
            @PathVariable Long cursoId,
            @RequestParam DiaSemana dia) {
        try {
            return ResponseEntity.ok(moduloService.modulosDelCursoPorDiaConEstado(cursoId, dia));
        } catch (ModuloException e) {
            return ResponseEntity.status(422).body(
                    ModuloEstadoListResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .modulos(java.util.Collections.emptyList())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    ModuloEstadoListResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .modulos(java.util.Collections.emptyList())
                            .build()
            );
        }
    }

    @GetMapping("/curso/{cursoId}/estado/semana")
    @Operation(summary = "Devuelve los módulos del colegio con su estado (ocupado/libre) para un curso en toda la semana")
    public ResponseEntity<ModuloEstadoSemanaResponse> estadoSemana(@PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(moduloService.modulosDelCursoSemanaConEstado(cursoId));
        } catch (ModuloException e) {
            Map<DiaSemana, List<com.grup14.luterano.dto.modulo.ModuloEstadoDto>> vacio = new EnumMap<>(DiaSemana.class);
            for (DiaSemana d : DiaSemana.values()) vacio.put(d, Collections.emptyList());
            return ResponseEntity.status(422).body(
                    ModuloEstadoSemanaResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .modulosPorDia(vacio)
                            .build()
            );
        } catch (Exception e) {
            Map<DiaSemana, List<com.grup14.luterano.dto.modulo.ModuloEstadoDto>> vacio = new EnumMap<>(DiaSemana.class);
            for (DiaSemana d : DiaSemana.values()) vacio.put(d, Collections.emptyList());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ModuloEstadoSemanaResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .modulosPorDia(vacio)
                            .build()
            );
        }
    }

    @GetMapping("/reservas/estado")
    @Operation(summary = "Devuelve los módulos con su estado de ocupación para un espacio áulico y fecha específica")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<ModuloReservaEstadoResponse> obtenerModulosConReservas(
            @RequestParam Long espacioAulicoId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            return ResponseEntity.ok(moduloService.obtenerModulosConReservas(espacioAulicoId, fecha));
        } catch (ModuloException e) {
            return ResponseEntity.status(422).body(
                    ModuloReservaEstadoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .modulos(Collections.emptyList())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ModuloReservaEstadoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .modulos(Collections.emptyList())
                            .build()
            );
        }
    }
}
