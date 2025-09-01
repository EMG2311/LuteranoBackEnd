package com.grup14.luterano.specification;

import com.grup14.luterano.entities.Alumno;
import com.grup14.luterano.entities.enums.Division;
import org.springframework.data.jpa.domain.Specification;

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

    public static Specification<Alumno> cursoAñoEquals(Integer año) {
        return (root, query, cb) ->
                año == null ? null : cb.equal(root.get("cursoActual").get("año"), año);
    }

    public static Specification<Alumno> divisionEquals(Division division) {
        return (root, query, cb) ->
                division == null ? null : cb.equal(root.get("cursoActual").get("division"), division);
    }
}
