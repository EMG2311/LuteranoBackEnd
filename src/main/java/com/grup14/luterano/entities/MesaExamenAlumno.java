package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.CondicionRinde;
import com.grup14.luterano.entities.enums.EstadoConvocado;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MesaExamenAlumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Alumno alumno;

    @ManyToOne(optional = false)
    private MesaExamen mesaExamen;

    @Enumerated(EnumType.STRING)
    private EstadoConvocado estado;  // NEW

    @Enumerated(EnumType.STRING)
    private CondicionRinde condicionRinde; // COLOQUIO o EXAMEN

    private Integer notaFinal;       // rename
    @ManyToOne(optional = false)
    private TurnoExamen turno;

    public boolean estaAprobado() {
        return notaFinal != null && notaFinal >= 6.0f;
    }
}