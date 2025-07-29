package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @ManyToOne
    private HistorialCurso historialCurso;


    @OneToMany(mappedBy = "historialCalificaciones", cascade = CascadeType.ALL)
    private List<Calificacion> calificaciones;
}
