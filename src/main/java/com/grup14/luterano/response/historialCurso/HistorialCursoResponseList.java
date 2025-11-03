package com.grup14.luterano.response.historialCurso;

import com.grup14.luterano.dto.HistorialCursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistorialCursoResponseList {
    private List<HistorialCursoDto> historialCursos;
    private Integer code;
    private String mensaje;
}
