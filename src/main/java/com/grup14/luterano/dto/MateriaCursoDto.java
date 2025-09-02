package com.grup14.luterano.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@Builder@AllArgsConstructor@NoArgsConstructor
public class MateriaCursoDto {
    private Long id;

    private MateriaDto materia;
  //  private CursoDto curso;
    private Long cursoId;  // quiero solo el id del curso
    private DocenteDto docente;
}
