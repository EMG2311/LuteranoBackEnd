package com.grup14.luterano.dto;
import com.grup14.luterano.entities.Aula;
import com.grup14.luterano.entities.Materia;
import com.grup14.luterano.entities.enums.Nivel;
import com.grup14.luterano.entities.enums.Division;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoDto {

    private Long id;
    private int numero;
    private Division division;
    private Nivel nivel;
    private AulaDto aula;
    private List<MateriaCursoDto> dictados;
}
