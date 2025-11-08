package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.MesaExamenException;
import com.grup14.luterano.request.mesaExamen.AgregarConvocadosRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenCreateRequest;
import com.grup14.luterano.request.mesaExamen.MesaExamenUpdateRequest;
import com.grup14.luterano.response.mesaExamen.MesaExamenListResponse;
import com.grup14.luterano.response.mesaExamen.MesaExamenResponse;
import com.grup14.luterano.service.MesaExamenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/mesas")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')")
@Tag(name = "Mesa de Examen Controller", description = "CRUD de Mesas de Examen y convocados")
@RequiredArgsConstructor
public class MesaExamenController {

    private final MesaExamenService service;

    @PostMapping
    @Operation(summary = "Crear mesa")
    public ResponseEntity<MesaExamenResponse> crear(@RequestBody MesaExamenCreateRequest req) {
        try {
            return ResponseEntity.ok(service.crear(req));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @PutMapping
    @Operation(summary = "Actualizar mesa")
    public ResponseEntity<MesaExamenResponse> actualizar(@RequestBody MesaExamenUpdateRequest req) {
        try {
            return ResponseEntity.ok(service.actualizar(req));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mesa")
    public ResponseEntity<MesaExamenResponse> eliminar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.eliminar(id));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener mesa por id")
    public ResponseEntity<MesaExamenResponse> obtener(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.obtener(id));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @GetMapping("/materiaCurso/{materiaCursoId}")
    @Operation(summary = "Listar mesas por MateriaCurso")
    public ResponseEntity<MesaExamenListResponse> listarPorMateriaCurso(@PathVariable Long materiaCursoId) {
        try {
            return ResponseEntity.ok(service.listarPorMateriaCurso(materiaCursoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenListResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @GetMapping("/curso/{cursoId}")
    @Operation(summary = "Listar mesas por Curso")
    public ResponseEntity<MesaExamenListResponse> listarPorCurso(@PathVariable Long cursoId) {
        try {
            return ResponseEntity.ok(service.listarPorCurso(cursoId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenListResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    // ---- Convocados -----
    @PostMapping("/{mesaId}/convocados")
    @Operation(summary = "Agregar convocados", description = "Body: { turnoId, alumnoIds: [..] }")
    public ResponseEntity<MesaExamenResponse> agregarConvocados(@PathVariable Long mesaId, @RequestBody AgregarConvocadosRequest req) {
        try {
            return ResponseEntity.ok(service.agregarConvocados(mesaId, req));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @DeleteMapping("/{mesaId}/convocados/{alumnoId}")
    @Operation(summary = "Quitar convocado")
    public ResponseEntity<MesaExamenResponse> quitarConvocado(@PathVariable Long mesaId, @PathVariable Long alumnoId) {
        try {
            return ResponseEntity.ok(service.quitarConvocado(mesaId, alumnoId));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    // ---- Notas & Cierre -----
    @PostMapping("/{mesaId}/notasFinales")
    @Operation(summary = "Cargar notas finales", description = "Body: { alumnoId: notaFinal, ... }")
    public ResponseEntity<MesaExamenResponse> cargarNotasFinales(@PathVariable Long mesaId, @RequestBody Map<Long, Integer> notas) {
        try {
            return ResponseEntity.ok(service.cargarNotasFinales(mesaId, notas));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }

    @PostMapping("/{mesaId}/finalizar")
    @Operation(summary = "Finalizar mesa", description = "Pasa estado a FINALIZADA")
    public ResponseEntity<MesaExamenResponse> finalizar(@PathVariable Long mesaId) {
        try {
            return ResponseEntity.ok(service.finalizar(mesaId));
        } catch (MesaExamenException e) {
            return ResponseEntity.status(422).body(MesaExamenResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(MesaExamenResponse.builder().code(-2).mensaje("Error no controlado " + e.getMessage()).build());
        }
    }
}
