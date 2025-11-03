package com.grup14.luterano.response.Preceptor;

import com.grup14.luterano.dto.PreceptorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PreceptorResponseList {
    private List<PreceptorDto> preceptores;
    private Integer code;
    private String mensaje;
}
