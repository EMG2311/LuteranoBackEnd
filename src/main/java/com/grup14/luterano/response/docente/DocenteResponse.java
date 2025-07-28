package com.grup14.luterano.response.docente;

import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.entities.Materia;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Builder
@Data
public class DocenteResponse {
    private DocenteDto docente;
    private Integer code;
    private String mensaje;
}


