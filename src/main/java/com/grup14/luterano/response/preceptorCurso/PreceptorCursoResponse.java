package com.grup14.luterano.response.preceptorCurso;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.dto.PreceptorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreceptorCursoResponse {
    private PreceptorDto preceptor;
    private CursoDto curso;
    private String mensaje;
    private Integer code;
}