package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity@Data@AllArgsConstructor@NoArgsConstructor@Builder
public class HistorialCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Alumno alumno;

    @ManyToOne
    private Curso curso;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private float promedio;

    @ManyToOne
    private CicloLectivo cicloLectivo;

    @OneToMany(mappedBy = "historialCurso", cascade = CascadeType.ALL)
    private List<HistorialCalificaciones> historialesCalificaciones;
}