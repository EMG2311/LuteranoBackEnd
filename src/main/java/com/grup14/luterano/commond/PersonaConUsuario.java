package com.grup14.luterano.commond;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.TipoDoc;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@MappedSuperclass@SuperBuilder@Data
public class PersonaConUsuario extends Persona {
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
