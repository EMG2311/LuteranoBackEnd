package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.CalificacionException;
import com.grup14.luterano.request.calificacion.CalificacionRequest;
import com.grup14.luterano.request.calificacion.CalificacionUpdateRequest;
import com.grup14.luterano.response.calificaciones.CalificacionListResponse;
import com.grup14.luterano.response.calificaciones.CalificacionResponse;
import com.grup14.luterano.service.CalificacionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/calificaciones")
public class CalificacionController {

    private final CalificacionService calificacionService;

    public CalificacionController(CalificacionService calificacionService) {
        this.calificacionService = calificacionService;
    }

    @PostMapping("/create")
    @Operation(summary = "Crear calificación",
            description = "Crea una calificación validando que el alumno cursa la materia en el ciclo correspondiente a la fecha de la calificación.")
    public ResponseEntity<CalificacionResponse> create(@RequestBody @Validated CalificacionRequest request) {
        try {
            return ResponseEntity.ok(calificacionService.crearCalificacion(request));
        } catch (CalificacionException e) {
            return ResponseEntity.status(422).body(
                    CalificacionResponse.builder().calificacion(null).code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionResponse.builder().calificacion(null).code(-2)
                            .mensaje("Error no controlado " + e.getMessage()).build()
            );
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar calificación",
            description = "Actualiza la nota y/o fecha de una calificación existente del alumno para la materia.")
    public ResponseEntity<CalificacionResponse> update(@RequestBody @Valid CalificacionUpdateRequest request) {
        try {
            return ResponseEntity.ok(calificacionService.actualizar(request));
        } catch (CalificacionException e) {
            return ResponseEntity.status(422).body(
                    CalificacionResponse.builder().calificacion(null).code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionResponse.builder().calificacion(null).code(-2)
                            .mensaje("Error no controlado " + e.getMessage()).build()
            );
        }
    }

    @GetMapping("/alumno/{alumnoId}/materia/{materiaId}/{califId}")
    @Operation(summary = "Obtener calificación",
            description = "Obtiene una calificación del alumno para la materia indicada.")
    public ResponseEntity<CalificacionResponse> getOne(@PathVariable Long alumnoId,
                                                       @PathVariable Long materiaId,
                                                       @PathVariable Long califId) {
        try {
            return ResponseEntity.ok(calificacionService.obtener(alumnoId, materiaId, califId));
        } catch (CalificacionException e) {
            return ResponseEntity.status(422).body(
                    CalificacionResponse.builder().calificacion(null).code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionResponse.builder().calificacion(null).code(-2)
                            .mensaje("Error no controlado " + e.getMessage()).build()
            );
        }
    }

    @DeleteMapping("/alumno/{alumnoId}/materia/{materiaId}/{califId}")
    @Operation(summary = "Eliminar calificación",
            description = "Elimina una calificación del alumno para la materia indicada.")
    public ResponseEntity<CalificacionResponse> delete(@PathVariable Long alumnoId,
                                                       @PathVariable Long materiaId,
                                                       @PathVariable Long califId) {
        try {
            return ResponseEntity.ok(calificacionService.eliminar(alumnoId, materiaId, califId));
        } catch (CalificacionException e) {
            return ResponseEntity.status(422).body(
                    CalificacionResponse.builder().calificacion(null).code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionResponse.builder().calificacion(null).code(-2)
                            .mensaje("Error no controlado " + e.getMessage()).build()
            );
        }
    }


    @GetMapping("/alumno/{alumnoId}")
    @Operation(summary = "Listar calificaciones del alumno por año (opcional etapa)",
            description = "Lista calificaciones del alumno en todas sus materias para un año dado; permite filtrar por etapa.")
    public ResponseEntity<CalificacionListResponse> listByYear(@PathVariable Long alumnoId,
                                                               @RequestParam Integer anio,
                                                               @RequestParam(required = false) Integer etapa) {
        try {
            CalificacionListResponse resp = (etapa != null)
                    ? calificacionService.listarPorAnioYEtapa(alumnoId, anio, etapa)
                    : calificacionService.listarPorAnio(alumnoId, anio);
            return ResponseEntity.ok(resp);
        } catch (CalificacionException e) {
            return ResponseEntity.status(422).body(
                    CalificacionListResponse.builder()
                            .calificaciones(List.of()).code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionListResponse.builder()
                            .calificaciones(List.of()).code(-2)
                            .mensaje("Error no controlado " + e.getMessage()).build()
            );
        }
    }

    @GetMapping("/alumno/{alumnoId}/materia/{materiaId}/all")
    @Operation(summary = "Listar todas las calificaciones por materia (sin filtros)",
            description = "Equivalente a listar por materia sin parámetros, expuesto como ruta explícita.")
    public ResponseEntity<CalificacionListResponse> listPorMateria(@PathVariable Long alumnoId,
                                                              @PathVariable Long materiaId) {
        try {
            return ResponseEntity.ok(calificacionService.listarPorMateria(alumnoId, materiaId));
        } catch (CalificacionException e) {
            return ResponseEntity.status(422).body(
                    CalificacionListResponse.builder()
                            .calificaciones(List.of()).code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    CalificacionListResponse.builder()
                            .calificaciones(List.of()).code(-2)
                            .mensaje("Error no controlado " + e.getMessage()).build()
            );
        }
    }
}
