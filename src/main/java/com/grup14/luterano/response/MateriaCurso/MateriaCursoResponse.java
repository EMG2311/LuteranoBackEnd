package com.grup14.luterano.response.MateriaCurso;

import com.grup14.luterano.dto.materiaCurso.MateriaCursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MateriaCursoResponse {
    private MateriaCursoDto materiaCursoDto;
    private Integer code;
    private String mensaje;
}
