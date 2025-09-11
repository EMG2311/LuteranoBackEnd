package com.grup14.luterano.response.docente;

import com.grup14.luterano.dto.docente.DocenteDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DocenteResponse {
    private DocenteDto docente;
    private Integer code;
    private String mensaje;
}


