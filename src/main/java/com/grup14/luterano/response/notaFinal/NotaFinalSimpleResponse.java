package com.grup14.luterano.response.notaFinal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotaFinalSimpleResponse {
    private Integer notaFinal;
    private Long alumnoId;
    private Long materiaId;
    private Integer anio;
    private int code;
    private String mensaje;
}