package com.grup14.luterano.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class ActaExamenDto {
    // Encabezado del acta
    private Long id;
    private String numeroActa;
    private LocalDate fechaCierre;
    private boolean cerrada;
    private String observaciones;
    
    // Información del curso
    private Integer cursoAnio;
    private String cursoDivision;
    private String cursoNivel;
    
    // Información de la materia
    private Long materiaId;
    private String materiaNombre;
    
    // Información del turno
    private Long turnoId;
    private String turnoNombre;
    private LocalDate fecha;
    
    // Detalle de docentes y alumnos
    private List<DocenteSimpleDto> docentes;
    private List<ItemAlumnoDto> alumnos;

    
    // DTOs anidados simples para respuesta
    @Data
    @Builder
    public static class DocenteSimpleDto {
        private Long id;
        private String nombreCompleto;
        private String dni;
    }

    @Data
    @Builder
    public static class ItemAlumnoDto {
        private Long alumnoId;
        private String apellido;
        private String nombre;
        private String dni;
        private Integer nota;
        private String observacion; // Aprobado / Desaprobado / Ausente
    }
}
