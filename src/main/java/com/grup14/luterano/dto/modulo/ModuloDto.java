package com.grup14.luterano.dto.modulo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuloDto {
    private Long id;
    private int orden;
    private String desde;
    private String hasta;
}
