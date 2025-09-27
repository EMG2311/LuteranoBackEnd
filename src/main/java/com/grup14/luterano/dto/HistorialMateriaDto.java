package com.grup14.luterano.dto;

import com.grup14.luterano.dto.materiaCurso.MateriaCursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialMateriaDto {
    private Long id;
    private HistorialCursoDto historialCurso;
    private MateriaCursoDto materiaCurso;

    private BigDecimal promedio;
    private List<CalificacionDto> calificaciones;
}
