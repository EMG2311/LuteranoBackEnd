package com.grup14.luterano.response.docente;

import com.grup14.luterano.dto.docente.InasistenciaDocenteDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class InasistenciaDocenteResponseList {

    private List<InasistenciaDocenteDto> inasistenciaDocenteDtos;
    private Integer code;
    private String mensaje;
}
