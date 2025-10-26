package com.grup14.luterano.response.mesaExamen;

import com.grup14.luterano.dto.MesaExamenDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MesaExamenResponse {
    private Integer code;
    private String mensaje;
    private MesaExamenDto mesa;
}
