package com.grup14.luterano.request.alumno;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class InasistenciaAlumnoRequest {

    private Long alumnoId;
    private EstadoAsistencia estado;


}
