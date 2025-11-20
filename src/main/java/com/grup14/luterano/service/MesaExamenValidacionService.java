package com.grup14.luterano.service;

import com.grup14.luterano.entities.MesaExamen;
import com.grup14.luterano.entities.MesaExamenAlumno;
import com.grup14.luterano.entities.MesaExamenDocente;

public interface MesaExamenValidacionService {
    
    /**
     * Valida que una mesa de examen cumpla con todas las reglas de negocio
     */
    void validarMesa(MesaExamen mesa);
    
    /**
     * Valida que se pueda agregar un docente a la mesa
     */
    void validarAgregarDocente(MesaExamen mesa, MesaExamenDocente docente);
    
    /**
     * Valida que se pueda inscribir un alumno a la mesa
     */
    void validarInscribirAlumno(MesaExamen mesa, MesaExamenAlumno alumno);
    
    /**
     * Valida la configuración de la mesa antes de guardar
     */
    void validarConfiguracionMesa(MesaExamen mesa);
    void validarConfiguracionMesa(MesaExamen mesa, boolean fechaObligatoria);
    /**
     * Valida que se pueda cambiar el tipo de mesa
     */
    void validarCambioTipoMesa(MesaExamen mesa, com.grup14.luterano.entities.enums.TipoMesa nuevoTipo);
    
    /**
     * Valida la asignación de docentes según el tipo de mesa
     */
    void validarAsignacionDocentes(MesaExamen mesa, java.util.List<Long> docenteIds, java.util.Set<Long> docentesQueDALaMateria);
}