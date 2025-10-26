package com.grup14.luterano.request.espacioAulico;

import com.grup14.luterano.dto.modulo.ModuloDto;
import com.grup14.luterano.entities.enums.EstadoReserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaEspacioFiltroRequest {

    private Long usuarioId;
    private EstadoReserva estado;
    private Long espacioAulicoId;
    private ModuloDto modulo;
    private LocalDate fecha;

}
