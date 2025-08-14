package com.grup14.luterano.request.curso;

import com.grup14.luterano.dto.CursoDto;
import com.grup14.luterano.response.curso.CursoResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class CursoRequest  extends CursoDto {

    public CursoResponse toResponse(String mensaje, Integer code) {
        return CursoResponse.builder()
                .curso(this)
                .code(code)
                .mensaje(mensaje)
                .build();
    }
}
