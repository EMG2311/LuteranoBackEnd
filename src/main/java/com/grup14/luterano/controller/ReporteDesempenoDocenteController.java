package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.ReporteDesempenoDocenteException;
import com.grup14.luterano.request.ReporteDesempenoDocente.ReporteDesempenoDocenteFiltroRequest;
import com.grup14.luterano.response.reporteDesempeñoDocente.ReporteDesempenoDocenteResponse;
import com.grup14.luterano.response.reporteNotas.CalificacionesCursoAnioResponse;
import com.grup14.luterano.service.ReporteDesempenoDocenteService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reporteDesempenoDocente")
@AllArgsConstructor
public class ReporteDesempenoDocenteController {

    private final ReporteDesempenoDocenteService reporteService;


    @PostMapping("/generar")
    public ResponseEntity<ReporteDesempenoDocenteResponse> generarReporte(
            @RequestBody ReporteDesempenoDocenteFiltroRequest filtros) {

        try {
            ReporteDesempenoDocenteResponse response = reporteService.generarReporteDesempenoDocente(filtros);
            return ResponseEntity.ok(response);

        } catch (ReporteDesempenoDocenteException e) {

            return ResponseEntity.status(422).body(
                    ReporteDesempenoDocenteResponse.builder().code(-1).mensaje(e.getMessage()).build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ReporteDesempenoDocenteResponse.builder().code(-2).mensaje("Error inesperado: " + e.getMessage()).build()
            );
        }
    }


}
