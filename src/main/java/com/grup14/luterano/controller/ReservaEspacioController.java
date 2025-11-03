package com.grup14.luterano.controller;

import com.grup14.luterano.exeptions.ReservaEspacioException;
import com.grup14.luterano.request.espacioAulico.DenegarEspacioAulicoRequest;
import com.grup14.luterano.request.espacioAulico.ReservaEspacioFiltroRequest;
import com.grup14.luterano.request.espacioAulico.ReservaEspacioRequest;
import com.grup14.luterano.response.espacioAulico.ReservaEspacioResponse;
import com.grup14.luterano.response.espacioAulico.ReservaEspacioResponseList;
import com.grup14.luterano.service.ReservaEspacioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
@RequiredArgsConstructor
public class ReservaEspacioController {

    private final ReservaEspacioService reservaEspacioService;

    // --- SOLICITUD DE RESERVA (Usuario Solicitante) ---
    @PostMapping("/solicitar")
    @PreAuthorize("isAuthenticated()") // Cualquier usuario logueado puede solicitar
    @Operation(summary = "Solicitar nueva reserva",
            description = "Crea una solicitud de reserva en estado PENDIENTE. Valida disponibilidad y capacidad.")
    public ResponseEntity<ReservaEspacioResponse> solicitarReserva(@Valid @RequestBody ReservaEspacioRequest request) {

        try {
            return ResponseEntity.ok(reservaEspacioService.solicitarReserva(request));

        } catch (ReservaEspacioException e) {
            // Maneja errores de lógica de negocio (ej: capacidad insuficiente, duplicidad)
            return ResponseEntity.status(422).body(ReservaEspacioResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            // Maneja cualquier otra excepción no esperada (ej: error de base de datos no controlado)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReservaEspacioResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    // --- CANCELAR (Usuario Solicitante) ---
    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Cancelar reserva",
            description = "Permite al usuario cancelar su propia reserva si está PENDIENTE o APROBADA.")
    public ResponseEntity<ReservaEspacioResponse> cancelarReserva(
            @PathVariable Long id) { // Este ID debe coincidir con el usuario autenticado
        try {
            return ResponseEntity.ok(reservaEspacioService.cancelarReserva(id));

        } catch (ReservaEspacioException e) {
            return ResponseEntity.status(422).body(ReservaEspacioResponse.builder()
                    .code(-1)
                    .mensaje(e.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReservaEspacioResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list-reservas")
    @Operation(summary = "Listar todas las reservas",
            description = "Permite listar todas las reservas sin filtros.")
    public ResponseEntity<ReservaEspacioResponseList> listReservas() {
        try {
            return ResponseEntity.ok(reservaEspacioService.listReservas());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReservaEspacioResponseList.builder()
                            .reservaEspacioDtos(List.of())
                            .code(-2)
                            .mensaje("Error no controlado: " + e.getMessage())
                            .build());
        }
    }

    // --- RESERVAS CON FILTRO ---
    @GetMapping("/filtros")
    @Operation(summary = "Listar reservas",
            description = "Permite listar reservas por filtro. Si no se especifican filtros, lista todas las reservas.")
    public ResponseEntity<ReservaEspacioResponseList> obtenerReservas(@RequestBody @Validated ReservaEspacioFiltroRequest filtros) {
        try {
            return ResponseEntity.ok(reservaEspacioService.obtenerReservas(filtros));
        } catch (ReservaEspacioException e) {
            return ResponseEntity.status(422).body(
                    ReservaEspacioResponseList.builder()
                            .reservaEspacioDtos(List.of())
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build());
        } catch (Exception d) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ReservaEspacioResponseList.builder()
                            .reservaEspacioDtos(List.of())
                            .code(-2)
                            .mensaje(d.getMessage())
                            .build());
        }
    }


    // ---GESTIÓN: APROBAR (Administrativo) ---
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR')")
    @Operation(summary = "Aprobar reserva",
            description = "Cambia el estado de una reserva PENDIENTE a APROBADA.")
    public ResponseEntity<ReservaEspacioResponse> aprobarReserva(@PathVariable Long id) {
        try {
            ReservaEspacioResponse dto = reservaEspacioService.aprobarReserva(id);
            return ResponseEntity.ok(dto);
        } catch (ReservaEspacioException e) {
            return ResponseEntity.status(422).body(ReservaEspacioResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReservaEspacioResponse.builder().code(-2).mensaje("Error interno del servidor al aprobar la reserva.").build());
        }
    }

    // -- GESTIÓN: DENEGAR (Administrativo) ---
    @PatchMapping("/{id}/denegar")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'PRECEPTOR')")
    @Operation(summary = "Denegar reserva",
            description = "Cambia el estado de una reserva PENDIENTE a DENEGADA, requiere motivo.")
    public ResponseEntity<ReservaEspacioResponse> denegarReserva(
            @PathVariable Long id,
            @Valid @RequestBody DenegarEspacioAulicoRequest request) {
        try {
            ReservaEspacioResponse dto = reservaEspacioService.denegarReserva(id, request.getMotivo());
            return ResponseEntity.ok(dto);
        } catch (ReservaEspacioException e) {
            return ResponseEntity.status(422).body(ReservaEspacioResponse.builder().code(-1).mensaje(e.getMessage()).build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ReservaEspacioResponse.builder().code(-2).mensaje("Error interno del servidor al denegar la reserva.").build());
        }
    }


}
