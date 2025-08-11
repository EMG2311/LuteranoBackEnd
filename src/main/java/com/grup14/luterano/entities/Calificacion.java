package com.grup14.luterano.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Builder@Data
public class Calificacion {
    @Id
    @GeneratedValue
    private Long id;

    @Max(value = 10,message = "La nota no puede ser mayor a 10")
    @Min(value = 1,message = "La nota no puede ser menor a 1")
    private float nota;

    private int numeroNota;

    @Max(value = 10,message = "La nota no puede ser mayor a 10")
    @Min(value = 1,message = "La nota no puede ser menor a 1")
    private float PG;

    private LocalDate fecha;

    @ManyToOne
    private Alumno alumno;

    @ManyToOne
    private Materia materia;

    @ManyToOne
    private CicloLectivo cicloLectivo;
    @ManyToOne
    private HistorialCalificaciones historialCalificaciones;

}