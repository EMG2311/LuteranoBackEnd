package com.grup14.luterano.dto;

import com.grup14.luterano.commond.PersonaConUsuario;
import com.grup14.luterano.commond.PersonaConUsuarioDto;
import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.entities.Materia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@SuperBuilder@Data@NoArgsConstructor@AllArgsConstructor
public class DocenteDto extends PersonaConUsuarioDto {
    private Set<Materia> materias;
}
