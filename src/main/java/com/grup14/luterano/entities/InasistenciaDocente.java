package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Inasistencia;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

public class InasistenciaDocente extends Inasistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Docente docente;
}
