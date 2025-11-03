package com.grup14.luterano.response.tutor;

import com.grup14.luterano.dto.TutorDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TutorResponse {
    private TutorDto tutor;
    private Integer code;
    private String mensaje;
}
