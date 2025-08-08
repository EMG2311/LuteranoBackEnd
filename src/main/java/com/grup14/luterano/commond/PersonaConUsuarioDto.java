package com.grup14.luterano.commond;

import com.grup14.luterano.dto.UserDto;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.TipoDoc;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PersonaConUsuarioDto extends PersonaDto {
    private UserDto user;

}
