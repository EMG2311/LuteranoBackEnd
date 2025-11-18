package com.grup14.luterano.response.asistenciaDocente;

import com.grup14.luterano.dto.asistencia.AsistenciaDocenteDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AsistenciaDocenteResponseList {
    private List<AsistenciaDocenteDto> items;
    private Integer code;
    private String mensaje;
}
