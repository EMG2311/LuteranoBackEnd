package com.grup14.luterano.response.aula;

import com.grup14.luterano.dto.AulaDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AulaResponse {

    private AulaDto aula;
    private Integer code;
    private String mensaje;

}
