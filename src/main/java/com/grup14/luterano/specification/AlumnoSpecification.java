package com.grup14.luterano.specification;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.enums.Division;
import com.grup14.luterano.entities.enums.EstadoAlumno;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AlumnoSpecification {

    public static Specification<Alumno> nombreContains(String nombre) {
        return (root, query, cb) ->
                (nombre == null || nombre.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("nombre")), "%" + nombre.toLowerCase() + "%");
    }

    public static Specification<Alumno> apellidoContains(String apellido) {
        return (root, query, cb) ->
                (apellido == null || apellido.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("apellido")), "%" + apellido.toLowerCase() + "%");
    }

    public static Specification<Alumno> dniContains(String dni) {
        return (root, query, cb) ->
                (dni == null || dni.isBlank())
                        ? null
                        : cb.like(root.get("dni"), "%" + dni + "%"); // no hace falta lower porque es numérico, pero si es String se puede
    }

    public static Specification<Alumno> cursoAnioEquals(Integer anio) {
        return (root, query, cb) ->
                anio == null ? null : cb.equal(root.get("cursoActual").get("anio"), anio);
    }

    public static Specification<Alumno> divisionEquals(Division division) {
        return (root, query, cb) ->
                division == null ? null : cb.equal(root.get("cursoActual").get("division"), division);
    }

    public static Specification<Alumno> estadoNotIn(List<EstadoAlumno> estados) {
        return (root, query, cb) ->
                (estados == null || estados.isEmpty())
                        ? null
                        : cb.not(root.get("estado").in(estados));
    }

    public static Specification<Alumno> alumnosActivos() {
        return estadoNotIn(List.of(EstadoAlumno.EGRESADO, EstadoAlumno.BORRADO, EstadoAlumno.EXCLUIDO_POR_REPETICION));
    }
}
