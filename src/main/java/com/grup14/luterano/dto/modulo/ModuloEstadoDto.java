package com.grup14.luterano.dto.modulo;

import com.grup14.luterano.dto.HorarioClaseModuloDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuloEstadoDto {
    private ModuloDto modulo;
    private boolean ocupado;
    private HorarioClaseModuloDto horario;
}