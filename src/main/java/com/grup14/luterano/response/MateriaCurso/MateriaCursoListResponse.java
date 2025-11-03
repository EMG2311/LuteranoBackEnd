package com.grup14.luterano.response.MateriaCurso;

import com.grup14.luterano.dto.materiaCurso.MateriaCursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MateriaCursoListResponse {
    private List<MateriaCursoDto> materiaCursoDtoLis;
    private Integer code;
    private String mensaje;
}
