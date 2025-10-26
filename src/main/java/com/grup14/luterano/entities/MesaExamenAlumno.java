package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.EstadoConvocado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private Integer notaFinal;       // rename
    @ManyToOne(optional = false)
    private TurnoExamen turno;

    public boolean estaAprobado() {
        return notaFinal != null && notaFinal >= 6.0f;
    }
}