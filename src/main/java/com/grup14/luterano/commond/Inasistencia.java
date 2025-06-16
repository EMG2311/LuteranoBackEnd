package com.grup14.luterano.commond;

import com.grup14.luterano.entities.Preceptor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@MappedSuperclass
public class Inasistencia {

    private LocalDate fecha;
    private String detalle;
    @ManyToOne
    private Preceptor preceptor;
}