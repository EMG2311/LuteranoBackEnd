package com.grup14.luterano.request.docente;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InasistenciaDocenteUpdateRequest {

    private Long id;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    //NO ACTUALIZO EL DOCENTE NI EL PRECEPTOR
    //  private Long docenteId;  //PUEDO QUERER SU NOMBRE Y NO SU ID?
    //private Long preceptorId;  //QUIEN REGISTRO LA INASISTENCIA

}
