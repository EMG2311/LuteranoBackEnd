package com.grup14.luterano.service.implementation;

import com.grup14.luterano.entities.MesaExamen;
import com.grup14.luterano.entities.MesaExamenAlumno;
import com.grup14.luterano.entities.MesaExamenDocente;
import com.grup14.luterano.entities.enums.TipoMesa;
import com.grup14.luterano.service.MesaExamenValidacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MesaExamenValidacionServiceImpl implements MesaExamenValidacionService {

    @Override
    public void validarMesa(MesaExamen mesa) {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa de examen no puede ser null");
        }

        // 👉 para validación “completa” normal, la fecha es obligatoria
        validarConfiguracionMesa(mesa, true);

        // Validar docentes
        if (mesa.getDocentes() != null) {
            for (MesaExamenDocente docente : mesa.getDocentes()) {
                validarAgregarDocente(mesa, docente);
            }
        }

        // Validar alumnos
        if (mesa.getAlumnos() != null) {
            for (MesaExamenAlumno alumno : mesa.getAlumnos()) {
                validarInscribirAlumno(mesa, alumno);
            }
        }
    }

    @Override
    public void validarAgregarDocente(MesaExamen mesa, MesaExamenDocente docente) {
        if (mesa == null || docente == null) {
            throw new IllegalArgumentException("Mesa y docente no pueden ser null");
        }

        int cantidadDocentes = mesa.getDocentes() != null ? mesa.getDocentes().size() : 0;

        // Validar límite de docentes según tipo de mesa
        switch (mesa.getTipoMesa()) {
            case COLOQUIO -> {
                if (cantidadDocentes >= 1) {
                    throw new IllegalArgumentException("Una mesa de coloquio solo puede tener 1 docente");
                }
            }
            case EXAMEN -> {
                if (cantidadDocentes >= 3) {
                    throw new IllegalArgumentException("Una mesa de examen final puede tener máximo 3 docentes");
                }
            }
        }

        // Validar que el docente no esté ya asignado
        if (mesa.getDocentes() != null && mesa.getDocentes().stream()
                .anyMatch(d -> d.getDocente().getId().equals(docente.getDocente().getId()))) {
            throw new IllegalArgumentException("El docente ya está asignado a esta mesa");
        }
    }

    @Override
    public void validarInscribirAlumno(MesaExamen mesa, MesaExamenAlumno alumno) {
        if (mesa == null || alumno == null) {
            throw new IllegalArgumentException("Mesa y alumno no pueden ser null");
        }

        // Validar condición del alumno según tipo de mesa
        if (!mesa.puedeInscribirAlumno(alumno)) {
            String mensaje = switch (mesa.getTipoMesa()) {
                case COLOQUIO -> "Solo alumnos en condición de coloquio pueden inscribirse en mesas de coloquio";
                case EXAMEN -> "Error en la condición del alumno para examen final";
            };
            throw new IllegalArgumentException(mensaje);
        }

        // Validar que el alumno no esté ya inscrito
        if (mesa.getAlumnos() != null && mesa.getAlumnos().stream()
                .anyMatch(a -> a.getAlumno().getId().equals(alumno.getAlumno().getId()))) {
            throw new IllegalArgumentException("El alumno ya está inscrito en esta mesa");
        }
    }

    // 👉 método original, ahora delega a la nueva sobrecarga con fecha obligatoria
    @Override
    public void validarConfiguracionMesa(MesaExamen mesa) {
        validarConfiguracionMesa(mesa, true);
    }

    // 👉 NUEVA implementación flexible
    @Override
    public void validarConfiguracionMesa(MesaExamen mesa, boolean fechaObligatoria) {
        if (mesa == null) {
            throw new IllegalArgumentException("La mesa de examen no puede ser null");
        }

        if (mesa.getTipoMesa() == null) {
            throw new IllegalArgumentException("El tipo de mesa es obligatorio");
        }

        if (mesa.getMateriaCurso() == null) {
            throw new IllegalArgumentException("La materia-curso es obligatoria");
        }

        if (mesa.getTurno() == null) {
            throw new IllegalArgumentException("El turno de examen es obligatorio");
        }

        if (fechaObligatoria && mesa.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la mesa es obligatoria");
        }

        // Si querés validar que la fecha esté dentro del turno, hacelo solo si hay fecha
        /*
        if (mesa.getFecha() != null) {
            LocalDate f = mesa.getFecha();
            TurnoExamen t = mesa.getTurno();
            if (f.isBefore(t.getFechaInicio()) || f.isAfter(t.getFechaFin())) {
                throw new IllegalArgumentException("La fecha debe estar dentro del turno");
            }
        }
        */

        // Validaciones específicas por tipo
        switch (mesa.getTipoMesa()) {
            case COLOQUIO -> {
                log.debug("Validando mesa de coloquio - ID: {}", mesa.getId());
                // Validaciones específicas para coloquios si las hay
            }
            case EXAMEN -> {
                log.debug("Validando mesa de examen final - ID: {}", mesa.getId());
                // Validaciones específicas para exámenes finales si las hay
            }
        }
    }

    @Override
    public void validarCambioTipoMesa(MesaExamen mesa, TipoMesa nuevoTipo) {
        if (mesa == null || nuevoTipo == null) {
            throw new IllegalArgumentException("Mesa y nuevo tipo no pueden ser null");
        }

        // Si ya tiene el mismo tipo, no hay problema
        if (mesa.getTipoMesa() == nuevoTipo) {
            return;
        }

        // Verificar si tiene alumnos o docentes asignados
        boolean tieneAlumnos = mesa.getAlumnos() != null && !mesa.getAlumnos().isEmpty();
        boolean tieneDocentes = mesa.getDocentes() != null && !mesa.getDocentes().isEmpty();

        if (tieneAlumnos || tieneDocentes) {
            throw new IllegalArgumentException("No se puede cambiar el tipo de una mesa que ya tiene alumnos o docentes asignados");
        }
    }

    @Override
    public void validarAsignacionDocentes(MesaExamen mesa,
                                          java.util.List<Long> docenteIds,
                                          java.util.Set<Long> docentesQueDALaMateria) {
        if (mesa == null || docenteIds == null) {
            throw new IllegalArgumentException("Mesa y lista de docentes no pueden ser null");
        }

        switch (mesa.getTipoMesa()) {
            case COLOQUIO -> {
                // Para coloquio: exactamente 1 docente y debe ser de la materia
                if (docenteIds.size() != 1) {
                    throw new IllegalArgumentException("Una mesa de coloquio debe tener exactamente 1 docente");
                }

                Long docenteId = docenteIds.get(0);
                if (docentesQueDALaMateria == null || !docentesQueDALaMateria.contains(docenteId)) {
                    throw new IllegalArgumentException("En una mesa de coloquio, el docente debe dar la materia: " +
                            mesa.getMateriaCurso().getMateria().getNombre());
                }
            }
            case EXAMEN -> {
                // Para examen: exactamente 3 docentes y al menos uno debe ser de la materia
                if (docenteIds.size() != 3) {
                    throw new IllegalArgumentException("Una mesa de examen final debe tener exactamente 3 docentes");
                }

                if (docentesQueDALaMateria == null) {
                    throw new IllegalArgumentException("Debe asignar al menos un docente que dé la materia: " +
                            mesa.getMateriaCurso().getMateria().getNombre());
                }

                boolean tieneDocenteMateria = docenteIds.stream()
                        .anyMatch(docentesQueDALaMateria::contains);

                if (!tieneDocenteMateria) {
                    throw new IllegalArgumentException("Debe asignar al menos un docente que dé la materia: " +
                            mesa.getMateriaCurso().getMateria().getNombre());
                }
            }
        }
    }
}
