package com.grup14.luterano.response.actaExamen;


import com.grup14.luterano.dto.ActaExamenDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActaExamenResponse {
    private Integer code;
    private String mensaje;
    private ActaExamenDto acta;
}