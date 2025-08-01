package com.grup14.luterano.request.alumno;

import com.grup14.luterano.dto.AlumnoDto;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Data
@NoArgsConstructor
public class AlumnoRequest extends AlumnoDto {

    // para crear  un alumno ante una solicitud HTTP (POST) , recibe los DATOS ENTRADA del alumno que el cliente envía

    public AlumnoResponse toResponse(String mensaje, Integer code) {
        return AlumnoResponse.builder()
                .alumno(AlumnoDto.builder()
                        .nombre(this.getNombre())
                        .apellido(this.getApellido())
                        .genero(this.getGenero())
                        .tipoDoc(this.getTipoDoc())
                        .dni(this.getDni())
                        .email(this.getEmail())
                        .direccion(this.getDireccion())
                        .telefono(this.getTelefono())
                        .fechaNacimiento(this.getFechaNacimiento())
                        .fechaIngreso(this.getFechaIngreso())
                        /// Campos específicos de Alumno
                        .cursoActual(this.getCursoActual())
                        .estado(this.getEstado()) // Obtiene el valor del enum EstadoAlumno
                        .tutor(this.getTutor())
                        .build())
                .code(code)
                .mensaje(mensaje)
                .build();

    }
}
