package com.grup14.luterano.controller;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class Prueba {
    @Autowired
    private AlumnoRepository alumnoRepository;

    @PostMapping("/hola")
    public ResponseEntity<Alumno> prueba(){

        return ResponseEntity.ok(alumnoRepository.save(Alumno.builder().build()));
    }
}
