package com.grup14.luterano.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "asistencia_alumno",
        uniqueConstraints = @UniqueConstraint(columnNames = {"alumno_id", "fecha"}))
public class AsistenciaAlumno extends AsistenciaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Alumno alumno;
}
