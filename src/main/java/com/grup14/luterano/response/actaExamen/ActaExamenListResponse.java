package com.grup14.luterano.response.actaExamen;

import com.grup14.luterano.dto.ActaExamenDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ActaExamenListResponse {
    private Integer code;
    private String mensaje;
    private Integer total;
    private List<ActaExamenDto> actas;
}
