package com.grup14.luterano.response.cicloLectivo;

import com.grup14.luterano.dto.CicloLectivoDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CicloLectivoResponse {

    private CicloLectivoDto CicloLectivo;
    private Integer code;
    private String mensaje;
}
