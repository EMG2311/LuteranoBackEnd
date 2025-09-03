package com.grup14.luterano.request.curso;

import com.grup14.luterano.dto.AulaDto;
import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.dto.MateriaCursoDto;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import com.grup14.luterano.response.curso.CursoResponse;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

//@SuperBuilder @Data
//@NoArgsConstructor
@Getter
@Setter
@Builder
public class CursoRequest  {

    private int numero;
    private Division division;
    private Nivel nivel;
    private Long aulaId; // quiero solo el id del aula

}
