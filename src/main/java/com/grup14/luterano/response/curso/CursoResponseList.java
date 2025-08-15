package com.grup14.luterano.response.curso;


import com.grup14.luterano.dto.CursoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CursoResponseList {
    private List<CursoDto> cursoDtos;
    private Integer code;
    private String mensaje;
}
