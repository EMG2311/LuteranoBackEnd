package com.grup14.luterano.dto.promocion;

import com.grup14.luterano.entities.enums.EstadoMateriaAlumno;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MateriaEstadoFinalDto {
    private Long materiaId;
    private String materiaNombre;
    private Integer notaFinal;
    private EstadoMateriaAlumno estadoFinal;
}
