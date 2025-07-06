package com.grup14.luterano.response.docente;

import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.entities.Materia;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder@Data
public class DocenteResponse extends PersonaDto {
    private List<Materia> materias;
    private Integer code;
    private String mensaje;
}


