package com.grup14.luterano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@Builder@AllArgsConstructor@NoArgsConstructor
public class MateriaCursoDto {
    private Long id;

    private MateriaDto materia;
    private CursoDto curso;
    private DocenteDto docente;
}
