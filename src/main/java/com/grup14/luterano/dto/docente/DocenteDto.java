package com.grup14.luterano.dto.docente;

import com.grup14.luterano.commond.PersonaConUsuarioDto;
import com.grup14.luterano.dto.materiaCurso.MateriaCursoDto;
import com.grup14.luterano.dto.materiaCurso.MateriaCursoLigeroDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder@Data@NoArgsConstructor@AllArgsConstructor
public class DocenteDto extends PersonaConUsuarioDto {
    private List<MateriaCursoLigeroDto> dictados;
}
