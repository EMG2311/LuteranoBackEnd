package com.grup14.luterano.controller;

import com.grup14.luterano.dto.notaFinal.NotaFinalDetalleDto;
import com.grup14.luterano.response.notaFinal.NotaFinalResponse;
import com.grup14.luterano.response.notaFinal.NotaFinalSimpleResponse;
import com.grup14.luterano.service.NotaFinalService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notas-finales")
@RequiredArgsConstructor
public class NotaFinalController {

    private final NotaFinalService notaFinalService;

    @GetMapping("/alumno/{alumnoId}/materia/{materiaId}")
    @Operation(summary = "Obtiene la nota final de una materia para un alumno", 
               description = "Calcula la nota final basándose en mesa de examen (si existe) o PG truncado")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<NotaFinalResponse> obtenerNotaFinal(
            @PathVariable Long alumnoId,
            @PathVariable Long materiaId,
            @RequestParam int anio) {
        try {
            NotaFinalDetalleDto detalle = notaFinalService.obtenerNotaFinalDetallada(alumnoId, materiaId, anio);
            
            if (detalle == null) {
                return ResponseEntity.status(404).body(NotaFinalResponse.builder()
                        .notaFinal(null)
                        .origen(null)
                        .code(-1)
                        .mensaje("No se encontraron datos para calcular la nota final")
                        .build());
            }

            return ResponseEntity.ok(NotaFinalResponse.builder()
                    .notaFinal(detalle.getNotaFinal())
                    .origen(detalle.getOrigen())
                    .promedioGeneral(detalle.getPromedioGeneral())
                    .mesaExamenId(detalle.getMesaExamenId())
                    .alumnoId(alumnoId)
                    .materiaId(materiaId)
                    .anio(anio)
                    .code(0)
                    .mensaje("OK")
                    .build());

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(422).body(NotaFinalResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(NotaFinalResponse.builder()
                    .code(-2)
                    .mensaje("Error no controlado: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/simple/alumno/{alumnoId}/materia/{materiaId}")
    @Operation(summary = "Obtiene solo el valor numérico de la nota final", 
               description = "Devuelve únicamente la nota final como número entero")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR', 'DOCENTE')")
    public ResponseEntity<NotaFinalSimpleResponse> obtenerNotaFinalSimple(
            @PathVariable Long alumnoId,
            @PathVariable Long materiaId,
            @RequestParam int anio) {
        try {
            Integer notaFinal = notaFinalService.calcularNotaFinal(alumnoId, materiaId, anio);
            
            return ResponseEntity.ok(NotaFinalSimpleResponse.builder()
                    .notaFinal(notaFinal)
                    .alumnoId(alumnoId)
                    .materiaId(materiaId)
                    .anio(anio)
                    .code(0)
                    .mensaje("OK")
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(500).body(NotaFinalSimpleResponse.builder()
                    .code(-2)
                    .mensaje("Error no controlado: " + e.getMessage())
                    .build());
        }
    }
}