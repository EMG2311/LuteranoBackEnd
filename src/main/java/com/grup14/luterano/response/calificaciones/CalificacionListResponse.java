package com.grup14.luterano.response.calificaciones;

import com.grup14.luterano.dto.CalificacionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class CalificacionListResponse {
    private List<CalificacionDto> calificaciones;
    private Integer code;
    private String mensaje;
}
