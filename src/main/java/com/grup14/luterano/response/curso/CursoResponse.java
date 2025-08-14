package com.grup14.luterano.response.curso;


import com.grup14.luterano.dto.CursoDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CursoResponse {

    private CursoDto curso;
    private Integer code;
    private String mensaje;
}
