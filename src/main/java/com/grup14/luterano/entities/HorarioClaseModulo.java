package com.grup14.luterano.entities;

import com.grup14.luterano.entities.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "horario_clase_mod",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mc_dia_mod", columnNames = {"materia_curso_id","dia_semana","modulo_id"})
        },
        indexes = {
                @Index(name = "idx_mc_dia", columnList = "materia_curso_id,dia_semana"),
                @Index(name = "idx_modulo", columnList = "modulo_id")
        }
)
public class HorarioClaseModulo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING) @Column(name="dia_semana", nullable=false)
    private DiaSemana diaSemana;

    @ManyToOne(optional=false) @JoinColumn(name="modulo_id", nullable=false)
    private Modulo modulo;

    @ManyToOne(optional=false) @JoinColumn(name="materia_curso_id", nullable=false)
    private MateriaCurso materiaCurso;

    // AHORA OPCIONAL: se completa cuando asignes docente a MateriaCurso
    @ManyToOne(optional = true) @JoinColumn(name="docente_id")
    private Docente docente;

    @Transient public Curso getCurso(){ return materiaCurso.getCurso(); }
    @Transient public Materia getMateria(){ return materiaCurso.getMateria(); }
}
