package com.grup14.luterano.commond;

import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.TipoDoc;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@Getter@Setter@AllArgsConstructor@NoArgsConstructor
@MappedSuperclass@SuperBuilder@Data
public class PersonaConUsuario {
    private String nombre;
    private String apellido;
    private GeneroEnum genero;
    private TipoDoc tipoDoc;
    private String dni;
    private String email;
    private String direccion;
    private String telefono;
    private Date fechaNacimiento;
    private Date fechaIngreso;
    private boolean active;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    public String getNombre() {
        return user != null ? user.getName() : null;
    }

    public String getApellido() {
        return user != null ? user.getLastName() : null;
    }
}
