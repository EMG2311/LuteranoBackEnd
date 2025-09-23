package com.grup14.luterano.entities;

import com.grup14.luterano.commond.GeneroEnum;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AlumnoTest {

    @Test
    void crearAlumnoYVerificarCampos() {
        Alumno alumno = new Alumno();
        alumno.setId(1L);
        alumno.setNombre("Agostina");   // heredado de Persona
        alumno.setApellido("Torres");   // heredado de Persona
        alumno.setDni("12345678");      // heredado de Persona
        alumno.setGenero(GeneroEnum.FEMENINO);
        alumno.setEstado(EstadoAlumno.REGULAR);

        assertEquals(1L, alumno.getId());
        assertEquals("Agostina", alumno.getNombre());
        assertEquals("Torres", alumno.getApellido());
        assertEquals("12345678", alumno.getDni());
        assertEquals(GeneroEnum.FEMENINO, alumno.getGenero());
        assertEquals(EstadoAlumno.REGULAR, alumno.getEstado());
    }

    @Test
    void verificarListasIniciales() {
        Alumno alumno = new Alumno();
        assertNotNull(alumno.getHistorialCursos());
        assertTrue(alumno.getHistorialCursos().isEmpty());
    }
}
