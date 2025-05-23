package com.grup14.luterano.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="Alumnos")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

}
