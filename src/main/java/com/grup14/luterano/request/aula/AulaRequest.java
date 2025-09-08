package com.grup14.luterano.request.aula;


import com.grup14.luterano.dto.AulaDto;
import com.grup14.luterano.response.aula.AulaResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class AulaRequest extends AulaDto {

    public AulaResponse toResponse(String mensaje, Integer code) {
        return AulaResponse.builder()
                .aula(this)
                .code(code)
                .mensaje(mensaje)
                .build();
    }

}
