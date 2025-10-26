package com.grup14.luterano.response.espacioAulico;

import com.grup14.luterano.dto.EspacioAulicoDto;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EspacioAulicoResponse {

    private EspacioAulicoDto espacioAulicoDto;
    private int code;
    private String mensaje;
}
