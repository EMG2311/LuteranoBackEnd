package com.grup14.luterano.request.docente;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class InasistenciaDocenteRequest  {

    private Long docenteId;
    private EstadoAsistencia estado;


}
