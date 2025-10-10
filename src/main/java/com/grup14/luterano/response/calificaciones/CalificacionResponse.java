package com.grup14.luterano.response.calificaciones;

import com.grup14.luterano.dto.calificaciones.CalificacionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@Builder@NoArgsConstructor@AllArgsConstructor
public class CalificacionResponse {
    private CalificacionDto calificacion;
    private Integer code;
    private String mensaje;
}
