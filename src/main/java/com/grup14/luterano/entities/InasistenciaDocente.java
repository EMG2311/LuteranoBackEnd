package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Inasistencia;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class InasistenciaDocente  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoAsistencia estado;

    @ManyToOne
    private User usuario;

    @ManyToOne
    private Docente docente;
}
