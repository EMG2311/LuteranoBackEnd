package com.grup14.luterano.entities;

import com.grup14.luterano.commond.Persona;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity@SuperBuilder@AllArgsConstructor
@NoArgsConstructor
@Data
public class Tutor extends Persona {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
