package com.grup14.luterano.response.turnoExamen;


import com.grup14.luterano.dto.TurnoDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TurnoResponse {
    private Integer code;
    private String mensaje;
    private TurnoDto turno;
}