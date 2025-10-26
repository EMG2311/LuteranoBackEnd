package com.grup14.luterano.request.espacioAulico;


import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEspacioRequest {

    private Long cursoId;
    private Long espacioAulicoId;
    private Long moduloId;

    @NotNull(message = "La fecha de la reserva es obligatoria.")
    @FutureOrPresent(message = "La fecha de la reserva no puede ser en el pasado.")
    private LocalDate fecha;

    //capturar el user que hace la solicitud desde el servicio, no desde el request

    private String motivoSolicitud;

}
