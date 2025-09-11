package com.grup14.luterano.dto.materiaCurso;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MateriaCursoLigeroDto {
    private Long id;
    private String materiaNombre;
    private Long cursoId;
}