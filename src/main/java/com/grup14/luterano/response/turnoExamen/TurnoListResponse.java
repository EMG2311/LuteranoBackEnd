package com.grup14.luterano.response.turnoExamen;

import com.grup14.luterano.dto.TurnoDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TurnoListResponse {
    private Integer code;
    private String mensaje;
    private List<TurnoDto> turnos;
}