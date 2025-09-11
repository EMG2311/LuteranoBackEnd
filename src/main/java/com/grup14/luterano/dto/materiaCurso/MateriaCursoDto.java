package com.grup14.luterano.dto.materiaCurso;

import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.dto.docente.DocenteDto;
import com.grup14.luterano.dto.docente.DocenteLigeroDto;
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
    private DocenteLigeroDto docente;
}
