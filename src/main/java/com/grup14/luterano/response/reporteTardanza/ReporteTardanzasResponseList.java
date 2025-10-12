package com.grup14.luterano.response.reporteTardanza;

import com.grup14.luterano.dto.reporteTardanza.TardanzaRowDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReporteTardanzasResponseList {
    private List<TardanzaRowDto> items;
    private Integer code;
    private String mensaje;
}
