package com.grup14.luterano.controller;

import com.grup14.luterano.request.promocion.PromocionMasivaRequest;
import com.grup14.luterano.response.promocion.PromocionMasivaResponse;
import com.grup14.luterano.service.PromocionMasivaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/promocion")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(
        name = "Promoción Masiva", 
        description = "Operaciones para promoción masiva de alumnos. " +
                "Acceso restringido exclusivamente a usuarios con rol ADMIN."
)
public class PromocionMasivaController {

    private final PromocionMasivaService promocionMasivaService;

    @PostMapping("/masiva")
    @Operation(summary = "Ejecutar promoción masiva",
            description = "Procesa todos los alumnos activos según reglas: <3 materias desaprobadas=promociona, >=3=repite, 6to=egresa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Promoción ejecutada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "403", description = "Sin permisos para esta operación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<PromocionMasivaResponse> ejecutarPromocionMasiva(@Valid @RequestBody PromocionMasivaRequest request) {
        try {
            PromocionMasivaResponse response = promocionMasivaService.ejecutarPromocionMasiva(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    PromocionMasivaResponse.builder()
                            .procesados(0)
                            .promocionados(0)
                            .repitentes(0)
                            .egresados(0)
                            .excluidos(0)
                            .noProcesados(0)
                            .dryRun(request.getDryRun())
                            .code(-1)
                            .mensaje("Error: " + e.getMessage())
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    PromocionMasivaResponse.builder()
                            .procesados(0)
                            .promocionados(0)
                            .repitentes(0)
                            .egresados(0)
                            .excluidos(0)
                            .noProcesados(0)
                            .dryRun(request.getDryRun())
                            .code(-2)
                            .mensaje("Error interno del servidor: " + e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/masiva/simulacion")
    @Operation(summary = "Simular promoción masiva",
            description = "Simula la promoción sin hacer cambios reales en la base de datos")
    public ResponseEntity<PromocionMasivaResponse> simularPromocionMasiva(@Valid @RequestBody PromocionMasivaRequest request) {
        // Forzar dryRun a true para simulación
        request.setDryRun(true);
        return ejecutarPromocionMasiva(request);
    }
}