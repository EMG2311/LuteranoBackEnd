package com.grup14.luterano.request.docente;

import com.grup14.luterano.dto.docente.DocenteDto;
import com.grup14.luterano.response.docente.DocenteResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data@NoArgsConstructor
public class DocenteRequest extends DocenteDto {

    public DocenteResponse toResponse(String mensaje,Integer code) {
        return DocenteResponse.builder()
                .docente(this)
                .code(code)
                .mensaje(mensaje)
                .build();
    }
}
