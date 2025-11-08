package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.CicloLectivoException;
import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponse;
import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponseList;
import com.grup14.luterano.service.CicloLectivoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/ciclos-lectivos")
@PreAuthorize("hasRole('ADMIN')")
public class CicloLectivoController {

    @Autowired
    private CicloLectivoService cicloLectivoService;

    @PostMapping("/siguiente")
    @Operation(summary = "Crea automáticamente el ciclo lectivo del año siguiente")
    public ResponseEntity<CicloLectivoResponse> crearSiguienteCicloLectivo() {
        try {
            CicloLectivoResponse nuevoCiclo = cicloLectivoService.crearSiguienteCicloLectivo();
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCiclo);
        } catch (CicloLectivoException e) {
            return ResponseEntity.status(422).body(
                    CicloLectivoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CicloLectivoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/manual/{anio}")
    @Operation(summary = "Crea manualmente un ciclo lectivo para un año específico")
    public ResponseEntity<CicloLectivoResponse> crearCicloLectivoPorAnio(@PathVariable int anio) {
        try {
            if (anio <= 0) {
                throw new CicloLectivoException("El año debe ser mayor a 0.");
            }
            CicloLectivoResponse nuevoCiclo = cicloLectivoService.crearCicloLectivoPorAnio(anio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCiclo);
        } catch (CicloLectivoException e) {
            return ResponseEntity.status(422).body(
                    CicloLectivoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CicloLectivoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un ciclo lectivo por ID", description = "Devuelve un ciclo lectivo específico por su ID.")
    public ResponseEntity<CicloLectivoResponse> getCicloLectivoById(@PathVariable Long id) {
        try {
            if (id == null) {
                throw new CicloLectivoException("Debe indicar el ID del ciclo lectivo.");
            }
            return ResponseEntity.ok(cicloLectivoService.getCicloLectivoById(id));
        } catch (CicloLectivoException e) {
            return ResponseEntity.status(422).body(
                    CicloLectivoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CicloLectivoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('DOCENTE') OR hasRole('PRECEPTOR')")
    @GetMapping("/list")
    @Operation(summary = "Lista de ciclos lectivos", description = "Devuelve la lista completa de ciclos lectivos.")
    public ResponseEntity<CicloLectivoResponseList> ListCiclosLectivos() {
        try {
            return ResponseEntity.ok(cicloLectivoService.ListCiclosLectivos());
        } catch (CicloLectivoException e) {
            return ResponseEntity.status(422).body(
                    CicloLectivoResponseList.builder()
                            .CicloLectivoDtos(Collections.emptyList())
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    CicloLectivoResponseList.builder()
                            .CicloLectivoDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build()
            );
        }
    }
}

