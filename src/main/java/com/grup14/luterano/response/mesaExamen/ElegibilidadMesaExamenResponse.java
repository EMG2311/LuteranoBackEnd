package com.grup14.luterano.response.mesaExamen;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ElegibilidadMesaExamenResponse {
    private Integer code;
    private String mensaje;
    private List<Map<String, Object>> alumnos;
}
