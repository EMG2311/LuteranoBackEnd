package com.grup14.luterano.controller;


import com.grup14.luterano.request.actaExamen.ActaCreateRequest;
import com.grup14.luterano.request.actaExamen.ActaUpdateRequest;
import com.grup14.luterano.response.actaExamen.ActaExamenListResponse;
import com.grup14.luterano.response.actaExamen.ActaExamenResponse;
import com.grup14.luterano.service.ActaExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/actas")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
@Tag(name = "Acta Examen Controller", description = "Generación y consulta de actas de examen")
@RequiredArgsConstructor
public class ActaExamenController {

    private final ActaExamenService service;

    // ---- Generar / Actualizar / Eliminar ----
    @PostMapping("/generar")
    @Operation(summary = "Generar acta para una mesa FINALIZADA", description = "Si ya existe, la devuelve")
    public ResponseEntity<ActaExamenResponse> generar(@RequestBody ActaCreateRequest req) {
        try {
            return ResponseEntity.ok(service.generar(req));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ActaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        }
    }

    @PutMapping
    @Operation(summary = "Actualizar acta (número/observaciones)")
    public ResponseEntity<ActaExamenResponse> actualizar(@RequestBody ActaUpdateRequest req) {
        try {
            return ResponseEntity.ok(service.actualizar(req));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ActaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar acta")
    public ResponseEntity<ActaExamenResponse> eliminar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.eliminar(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ActaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        }
    }

    // ---- Obtener ----
    @GetMapping("/{id}")
    @Operation(summary = "Obtener acta por id")
    public ResponseEntity<ActaExamenResponse> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtenerPorId(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ActaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        }
    }

    @GetMapping("/mesa/{mesaId}")
    @Operation(summary = "Obtener acta por mesaId")
    public ResponseEntity<ActaExamenResponse> obtenerPorMesa(@PathVariable Long mesaId) {
        try {
            return ResponseEntity.ok(service.obtenerPorMesa(mesaId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ActaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        }
    }

    @GetMapping("/numero/{numeroActa}")
    @Operation(summary = "Obtener acta por número exacto")
    public ResponseEntity<ActaExamenResponse> obtenerPorNumero(@PathVariable String numeroActa) {
        try {
            return ResponseEntity.ok(service.obtenerPorNumero(numeroActa));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(ActaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        }
    }

    // ---- Listados / Búsquedas ----
    @GetMapping("/buscar")
    @Operation(summary = "Buscar actas por número (like) -> ?q=ACTA-")
    public ResponseEntity<ActaExamenListResponse> buscar(@RequestParam String q) {
        try {
            return ResponseEntity.ok(service.buscarPorNumeroLike(q));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ActaExamenListResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @GetMapping("/turno/{turnoId}")
    @Operation(summary = "Listar actas por turno")
    public ResponseEntity<ActaExamenListResponse> listarPorTurno(@PathVariable Long turnoId) {
        try {
            return ResponseEntity.ok(service.listarPorTurno(turnoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ActaExamenListResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar actas por curso")
    public ResponseEntity<ActaExamenListResponse> listarPorCurso(@PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(service.listarPorCurso(cursoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ActaExamenListResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @GetMapping("/entre-fechas")
    @Operation(summary = "Listar actas entre fechas (yyyy-MM-dd)")
    public ResponseEntity<ActaExamenListResponse> listarEntreFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        try {
            return ResponseEntity.ok(service.listarEntreFechas(desde, hasta));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ActaExamenListResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }
}
