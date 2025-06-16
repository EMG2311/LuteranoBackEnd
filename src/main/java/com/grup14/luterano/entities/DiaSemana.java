package com.grup14.luterano.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity@Builder@Data@AllArgsConstructor@NoArgsConstructor
public class DiaSemana {
    @Id
    @GeneratedValue
    private Long id;
    private String nombre;
}
