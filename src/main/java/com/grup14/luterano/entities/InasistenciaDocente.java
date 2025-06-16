package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Inasistencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@AllArgsConstructor@Builder@Entity@NoArgsConstructor
public class InasistenciaDocente extends Inasistencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Docente docente;
}
