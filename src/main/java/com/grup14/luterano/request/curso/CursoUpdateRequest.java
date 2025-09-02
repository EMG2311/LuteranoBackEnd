package com.grup14.luterano.request.curso;


import com.grup14.luterano.dto.AulaDto;
import com.grup14.luterano.dto.MateriaCursoDto;
import com.grup14.luterano.dto.MateriaDto;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.Nivel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursoUpdateRequest {

    @NotNull(message = "El ID del curso no puede ser nulo")
    private Long id;

    private Integer numero;
    private Division division;
    private Nivel nivel;

    // aula y dictados deben actualizarse por separado?
   // private AulaDto aula;
    // private List<MateriaCursoDto> dictados;


}
