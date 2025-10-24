package com.grup14.luterano.request.espacioAulico;

import com.grup14.luterano.entities.Curso;
import com.grup14.luterano.entities.EspacioAulico;
import com.grup14.luterano.entities.Modulo;
import com.grup14.luterano.entities.enums.EstadoReserva;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ReservaEspacioUpdateRequest {

    @NotNull(message = "El ID de la reserva es obligatorio para actualizar.")
    private Long id;

    @FutureOrPresent(message = "La fecha de la reserva no puede ser en el pasado.")
    private LocalDate fecha;

    private Long cursoId;
    private Long espacioAulicoId;
    private Long moduloId;
    private String motivoSolicitud;


}
