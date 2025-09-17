package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Inasistencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InasistenciaDocente extends Inasistencia {

    @ManyToOne
    private Docente docente;
}
