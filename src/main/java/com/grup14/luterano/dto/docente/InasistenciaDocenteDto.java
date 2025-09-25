package com.grup14.luterano.dto.docente;

import com.grup14.luterano.entities.enums.EstadoAsistencia;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@SuperBuilder(toBuilder=true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InasistenciaDocenteDto {
    private Long id;
    private LocalDate fecha;
    private EstadoAsistencia estado;
    private Long docenteId;  //PUEDO QUERER SU NOMBRE Y NO SU ID?
    private Long usuarioId;  //QUIEN REGISTRO LA INASISTENCIA

}
