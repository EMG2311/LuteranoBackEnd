package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistorialCurso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Alumno alumno;

    @ManyToOne(optional = false)
    private Curso curso;

    @ManyToOne(optional = false)
    private CicloLectivo cicloLectivo;

    private LocalDate fechaDesde;
    private LocalDate fechaHasta;

    private BigDecimal promedio;

    @OneToMany(mappedBy = "historialCurso", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistorialMateria> historialMaterias = new ArrayList<>();
}