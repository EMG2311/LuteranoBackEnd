package com.grup14.luterano.response.modulo;

import com.grup14.luterano.dto.modulo.ModuloDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data@Builder
public class ModuloListResponse {
    private List<ModuloDto> modulos;
    private Integer code;
    private String mensaje;
}
