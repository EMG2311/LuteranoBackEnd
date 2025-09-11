package com.grup14.luterano.response.Preceptor;

import com.grup14.luterano.dto.PreceptorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class PreceptorResponse{
    private PreceptorDto preceptor;
    private Integer code;
    private String mensaje;
}
