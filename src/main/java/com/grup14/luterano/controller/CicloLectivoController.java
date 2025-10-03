package com.grup14.luterano.controller;

import com.grup14.luterano.dto.CicloLectivoDto;
import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponse;
import com.grup14.luterano.response.cicloLectivo.CicloLectivoResponseList;
import com.grup14.luterano.service.CicloLectivoService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/ciclos-lectivos")
//@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR')") //or hasRole('DOCENTE') or hasRole('PRECEPTOR') ????
public class CicloLectivoController {

    @Autowired
    private CicloLectivoService cicloLectivoService;


    // METODO 1: Crear el ciclo lectivo del año siguiente (Automático)
    @PostMapping("/siguiente")
    public ResponseEntity<CicloLectivoResponse> crearSiguienteCicloLectivo() {
        CicloLectivoResponse nuevoCiclo = cicloLectivoService.crearSiguienteCicloLectivo();
        return new ResponseEntity<>(nuevoCiclo, HttpStatus.CREATED);
    }

    // METODO 2: Crear un ciclo lectivo para un año específico (Manual)

    @PostMapping("/manual/{anio}")
    public ResponseEntity<CicloLectivoResponse> crearCicloLectivoPorAnio(@PathVariable int anio) {
        CicloLectivoResponse nuevoCiclo = cicloLectivoService.crearCicloLectivoPorAnio(anio);
        return new ResponseEntity<>(nuevoCiclo, HttpStatus.CREATED);
    }


    // METODO 3: Obtener un Ciclo Lectivo por ID

    @GetMapping("/{id}")
    @Operation(summary = "Obtiene un ciclo lectivo por ID", description = "Devuelve un ciclo lectivo específico por su ID.")
    public ResponseEntity<CicloLectivoResponse> getCicloLectivoById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(cicloLectivoService.getCicloLectivoById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CicloLectivoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado" + e.getMessage())
                            .build());
        }
    }

    // METODO 4: Obtener la lista completa de Ciclos Lectivos

    @GetMapping("/list")
    @Operation(summary = "Lista de ciclo lectivo ", description = "Devuelve lista de ciclos lectivos.")
        public ResponseEntity<CicloLectivoResponseList> ListCiclosLectivos() {
            try {
                return ResponseEntity.ok(cicloLectivoService.ListCiclosLectivos());
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(CicloLectivoResponseList.builder()
                                .CicloLectivoDtos(Collections.emptyList())
                                .code(-2)
                                .mensaje("Error no controlado" + e.getMessage())
                                .build());
            }

    }

}

