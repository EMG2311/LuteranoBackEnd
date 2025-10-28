package com.grup14.luterano.response.espacioAulico;

import com.grup14.luterano.dto.EspacioAulicoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EspacioAulicoResponseList {

    private List<EspacioAulicoDto> espacioAulicoDtos;
    private int code;
    private String mensaje;
}
