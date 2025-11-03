package com.grup14.luterano.commond;

import com.grup14.luterano.dto.UserDto;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonaConUsuarioDto extends PersonaDto {

    private UserDto user;

}
