package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Inasistencia;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class InasistenciaAlumno extends Inasistencia {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Alumno alumno;
}
