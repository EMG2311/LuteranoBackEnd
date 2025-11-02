package com.grup14.luterano.response.modulo;

import com.grup14.luterano.dto.modulo.ModuloReservaEstadoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuloReservaEstadoResponse {
    private List<ModuloReservaEstadoDto> modulos;
    private int code;
    private String mensaje;
}