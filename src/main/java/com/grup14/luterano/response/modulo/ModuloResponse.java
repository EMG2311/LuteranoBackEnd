package com.grup14.luterano.response.modulo;

import com.grup14.luterano.dto.modulo.ModuloDto;
import lombok.Builder;
import lombok.Data;

@Data@Builder
public class ModuloResponse {
    private ModuloDto modulo;
    private Integer code;
    private String mensaje;
}
