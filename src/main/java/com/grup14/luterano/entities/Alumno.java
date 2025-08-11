package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Persona;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity@Data@AllArgsConstructor@SuperBuilder
@NoArgsConstructor
public class Alumno extends Persona {
    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Curso cursoActual;

    @Enumerated(EnumType.STRING)
    private EstadoAlumno estado;

    @ManyToOne
   private Tutor tutor;
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL)
    private List<HistorialCurso> historialCursos = new ArrayList<>();

}