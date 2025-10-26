package com.grup14.luterano.response.mesaExamen;


import com.grup14.luterano.dto.MesaExamenDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MesaExamenListResponse {
    private Integer code;
    private String mensaje;
    private Integer total;
    private List<MesaExamenDto> mesas;
}
