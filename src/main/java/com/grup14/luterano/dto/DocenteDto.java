package com.grup14.luterano.dto;

import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.entities.Materia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
@SuperBuilder@Data@NoArgsConstructor@AllArgsConstructor
public class DocenteDto extends PersonaDto {
    private List<Materia> materias;
}
