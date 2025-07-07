package com.grup14.luterano.response.docente;

import com.grup14.luterano.commond.PersonaDto;
import com.grup14.luterano.dto.DocenteDto;
import com.grup14.luterano.entities.Materia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Builder
@AllArgsConstructor@NoArgsConstructor
@Data
public class DocenteResponseList {
    private List<DocenteDto> docenteDtos;
    private Integer code;
    private String mensaje;
}
