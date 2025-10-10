package com.grup14.luterano.request.horarioClaseModulo;

import com.grup14.luterano.entities.enums.DiaSemana;
import lombok.Builder;
import lombok.Data;

@Data@Builder
public class SlotHorarioRequest {
    private DiaSemana dia;
    private Long moduloId;
}
