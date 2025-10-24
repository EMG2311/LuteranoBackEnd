package com.grup14.luterano.response.espacioAulico;

import com.grup14.luterano.dto.ReservaEspacioDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ReservaEspacioResponse {

    private ReservaEspacioDto reservaEspacioDto;
    private int code;
    private String mensaje;

}
