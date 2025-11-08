package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Alumno extends Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Curso cursoActual;

    @Enumerated(EnumType.STRING)
    private EstadoAlumno estado;

    // Campos para control de repeticiones
    @Builder.Default
    private Integer cantidadRepeticiones = 0;

    @Builder.Default
    private Integer maxRepeticionesPermitidas = 2;

    @ManyToMany
    @JoinTable(
        name = "alumno_tutor",
        joinColumns = @JoinColumn(name = "alumno_id"),
        inverseJoinColumns = @JoinColumn(name = "tutor_id")
    )
    @Builder.Default
    private List<Tutor> tutores = new ArrayList<>();
    
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    @Builder.Default
    private List<HistorialCurso> historialCursos = new ArrayList<>();

    @OneToMany(mappedBy = "alumno")
    @Builder.Default
    private List<MesaExamenAlumno> mesasExamen = new ArrayList<>();
}