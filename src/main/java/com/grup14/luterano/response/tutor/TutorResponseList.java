package com.grup14.luterano.response.tutor;

import com.grup14.luterano.dto.TutorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TutorResponseList {
    private List<TutorDto> tutores;
    private Integer code;
    private String mensaje;
}
