package com.grup14.luterano.response.aula;

import com.grup14.luterano.dto.AulaDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AulaResponseList {
    private List<AulaDto> aulaDtos;
    private Integer code;
    private String mensaje;
}
