package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.EstadoMesaExamen;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MesaExamen {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDate fecha;
    @ManyToOne(optional = false)
    private TurnoExamen turno;
    @ManyToOne(optional = false)
    private MateriaCurso materiaCurso;

    @ManyToOne
    private Aula aula;

    @Enumerated(EnumType.STRING)
    private EstadoMesaExamen estado;

    @OneToMany(mappedBy = "mesaExamen", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 25)
    private List<MesaExamenAlumno> alumnos = new ArrayList<>();

    @OneToMany(mappedBy = "mesaExamen", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    @Builder.Default
    private List<MesaExamenDocente> docentes = new ArrayList<>();

    @OneToOne(mappedBy = "mesa", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ActaExamen acta;
}
