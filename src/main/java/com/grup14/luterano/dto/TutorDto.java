package com.grup14.luterano.dto;

import com.grup14.luterano.commond.PersonaDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TutorDto extends PersonaDto {
    
    private List<AlumnoDto> alumnos;
}
