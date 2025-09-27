package com.grup14.luterano.commond;

import com.grup14.luterano.entities.Preceptor;
import com.grup14.luterano.entities.User;
import com.grup14.luterano.entities.enums.EstadoAsistencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
@SuperBuilder
public class Inasistencia {

    private LocalDate fecha;

    @Enumerated(EnumType.STRING)
    private EstadoAsistencia estado;

    @ManyToOne
    private User usuario;
}