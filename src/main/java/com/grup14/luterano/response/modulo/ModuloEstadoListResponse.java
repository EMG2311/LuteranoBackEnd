package com.grup14.luterano.response.modulo;

import com.grup14.luterano.dto.modulo.ModuloEstadoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ModuloEstadoListResponse {
    private Integer code;
    private String mensaje;
    private List<ModuloEstadoDto> modulos;
}
