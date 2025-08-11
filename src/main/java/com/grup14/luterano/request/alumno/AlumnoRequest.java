package com.grup14.luterano.request.alumno;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class AlumnoRequest extends AlumnoDto {


    public AlumnoResponse toResponse(String mensaje, Integer code) {
        return AlumnoResponse.builder()
                .alumno(this)
                .code(code)
                .mensaje(mensaje)
                .build();

    }
}
