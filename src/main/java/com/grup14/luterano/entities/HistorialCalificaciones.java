package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity@Data@Builder@AllArgsConstructor@NoArgsConstructor
public class HistorialCalificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Alumno alumno;

    @ManyToOne
    private Materia materia;

    @ManyToOne
    private CicloLectivo cicloLectivo;

    private float promedio;
}
