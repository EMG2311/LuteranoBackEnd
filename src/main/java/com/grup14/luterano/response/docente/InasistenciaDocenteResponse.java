package com.grup14.luterano.response.docente;

import com.grup14.luterano.dto.docente.InasistenciaDocenteDto;
import com.grup14.luterano.entities.InasistenciaDocente;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class InasistenciaDocenteResponse {

    private InasistenciaDocenteDto InasistenciaDocente;
    private Integer code;
    private String mensaje;
}
