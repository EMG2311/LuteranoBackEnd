package com.grup14.luterano.dto.reporteDisponibilidad;

import com.grup14.luterano.entities.enums.DiaSemana;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaAgendaDto {
    private DiaSemana dia;
    private List<BloqueOcupadoDto> bloques;
    private double horasOcupadasDia;
}
