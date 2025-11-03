package com.grup14.luterano.response.horarioClaseModulo;

import com.grup14.luterano.dto.HorarioClaseModuloDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HorarioClaseModuloResponse {
    private Integer code;
    private String mensaje;
    private HorarioClaseModuloDto horarioClaseModuloDto;
    private List<String> slotsModificados;
    private List<String> slotsConConflicto;
}
