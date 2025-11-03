package com.grup14.luterano.response.modulo;

import com.grup14.luterano.dto.modulo.ModuloDto;
import com.grup14.luterano.entities.enums.DiaSemana;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class ModuloSemanaResponse {
    private Integer code;
    private String mensaje;
    private Map<DiaSemana, List<ModuloDto>> modulosPorDia;
}
