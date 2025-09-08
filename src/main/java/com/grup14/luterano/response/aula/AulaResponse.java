package com.grup14.luterano.response.aula;

import com.grup14.luterano.dto.AulaDto;
import com.grup14.luterano.request.aula.AulaRequest;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Builder
@Data
public class AulaResponse {

    private AulaDto aula;
    private Integer code;
    private String mensaje;

}
