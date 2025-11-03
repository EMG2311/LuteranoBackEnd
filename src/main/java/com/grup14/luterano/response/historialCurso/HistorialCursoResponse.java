package com.grup14.luterano.response.historialCurso;

import com.grup14.luterano.dto.HistorialCursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistorialCursoResponse {
    private HistorialCursoDto historialCurso;
    private Integer code;
    private String mensaje;
}
