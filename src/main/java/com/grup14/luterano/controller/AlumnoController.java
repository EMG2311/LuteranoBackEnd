package com.grup14.luterano.controller;


import com.grup14.luterano.exeptions.AlumnoException;
import com.grup14.luterano.exeptions.HistorialCursoException;
import com.grup14.luterano.request.alumno.AlumnoFiltrosRequest;
import com.grup14.luterano.request.alumno.AlumnoRequest;
import com.grup14.luterano.request.alumno.AlumnoUpdateRequest;
import com.grup14.luterano.request.alumno.AsignarTutoresRequest;
import com.grup14.luterano.request.historialCursoRequest.HistorialCursoRequest;
import com.grup14.luterano.response.alumno.AlumnoResponse;
import com.grup14.luterano.response.alumno.AlumnoResponseList;
import com.grup14.luterano.service.AlumnoReactivacionService;
import com.grup14.luterano.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.groups.Default;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/alumno")
@PreAuthorize("hasRole('ADMIN') or hasRole('DIRECTOR') or hasRole('PRECEPTOR')")
@Tag(
        name = "Alumno Controller",
        description = "Controlador encargado de la gestión de alumnos. " +
                "Acceso restringido a usuarios con rol ADMIN, DIRECTOR o PRECEPTOR."
)
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final AlumnoReactivacionService reactivacionService;

    public AlumnoController(AlumnoService alumnoService, AlumnoReactivacionService reactivacionService) {
        this.alumnoService = alumnoService;
        this.reactivacionService = reactivacionService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    @Operation(summary = "Crea un nuevo alumno",
        description = "Requiere que el usuario tenga un rol de ADMIN o DIRECTOR")
    public ResponseEntity<AlumnoResponse> createAlumno(@RequestBody @Validated AlumnoRequest alumnoRequest) {
        try {
            return ResponseEntity.ok(alumnoService.crearAlumno(alumnoRequest));
        } catch (AlumnoException e) {
            return ResponseEntity.status(422).body(
                    alumnoRequest.toResponse(e.getMessage(), -1));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(alumnoRequest.toResponse(e.getMessage(), -2));
        }
    }


    @PutMapping("/update")
    @Operation(summary = "Actualiza un alumno",
            description = "Actualiza un alumno con los datos que se envíen.")
    public ResponseEntity<AlumnoResponse> updateAlumno(
            @RequestBody @Validated({Default.class}) AlumnoUpdateRequest updateRequest) {
        try {
            // Llama al servicio para actualizar el alumno
            AlumnoResponse response = alumnoService.updateAlumno(updateRequest);
            return ResponseEntity.ok(response);
        } catch (AlumnoException d) {
            return ResponseEntity.status(422).body(AlumnoResponse.builder()
                    .code(-1)
                    .mensaje(d.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    @Operation(summary = "Elimina un alumno por ID",
        description = "Elimina un alumno si existe. Requiere que el usuario tenga rol de ADMIN o DIRECTOR.")
    public ResponseEntity<AlumnoResponse> deleteAlumno(@PathVariable Long id) {
        try {
            // Llama al servicio para eliminar el alumno por su ID
            AlumnoResponse response = alumnoService.deleteAlumno(id);
            return ResponseEntity.ok(response);
        } catch (AlumnoException d) {
            return ResponseEntity.status(422).body(AlumnoResponse.builder()
                    .code(-1)
                    .mensaje(d.getMessage())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    @Operation(summary = "Lista todos los alumnos")
    public ResponseEntity<AlumnoResponseList> listAlumnos() {
        try {
            return ResponseEntity.ok(alumnoService.listAlumnos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AlumnoResponseList.builder()
                            .alumnoDtos(Collections.emptyList())
                            .code(-2)
                            .mensaje("Error no controlado " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/filtros")
    @Operation(summary = "Lista alumnos con filtros dinámicos",
            description = "Permite filtrar por nombre, apellido, dni, año y división")
    public ResponseEntity<AlumnoResponseList> listarAlumnos(@RequestBody @Validated AlumnoFiltrosRequest filtros) {
        try {
            return ResponseEntity.ok(alumnoService.listAlumnos(filtros));
        } catch (AlumnoException e) {
            // error controlado (-1)
            return ResponseEntity.status(422).body(
                    AlumnoResponseList.builder()
                            .alumnoDtos(List.of())
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            // error no controlado (-2)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponseList.builder()
                            .alumnoDtos(List.of())
                            .code(-2)
                            .mensaje(e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/egresados")
    @Operation(summary = "Lista alumnos egresados",
            description = "Obtiene la lista de todos los alumnos que han egresado del colegio")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<AlumnoResponseList> listarAlumnosEgresados() {
        try {
            return ResponseEntity.ok(alumnoService.listAlumnosEgresados());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponseList.builder()
                            .alumnoDtos(List.of())
                            .code(-2)
                            .mensaje("Error interno del servidor: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/excluidos")
    @Operation(summary = "Lista alumnos excluidos por repetición",
            description = "Obtiene la lista de todos los alumnos excluidos por exceder el límite de repeticiones")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<AlumnoResponseList> listarAlumnosExcluidos() {
        try {
            return ResponseEntity.ok(alumnoService.listAlumnosExcluidos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponseList.builder()
                            .alumnoDtos(List.of())
                            .code(-2)
                            .mensaje("Error interno del servidor: " + e.getMessage())
                            .build()
            );
        }
    }


    @PostMapping("/asignarCursoAlumno")
    @Operation(
            summary = "Asigna un alumno a un curso",
            description = "Crea un nuevo registro de historial de curso para un alumno en un ciclo lectivo. " +
                    "Si el alumno ya tiene un curso asignado en ese ciclo, se cierra el historial anterior y se abre uno nuevo."
    )
    public ResponseEntity<AlumnoResponse> asignarCursoAlumno(
            @RequestBody @Validated HistorialCursoRequest request) {
        try {
            AlumnoResponse response = alumnoService.asignarCurso(request);
            return ResponseEntity.ok(response);
        } catch (HistorialCursoException e) {
            return ResponseEntity.status(422).body(
                    AlumnoResponse.builder()
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponse.builder()
                            .code(-2)
                            .mensaje("Error inesperado: " + e.getMessage())
                            .build()
            );
        }
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Busca un alumno por DNI")
    public ResponseEntity<AlumnoResponse> buscarPorDni(@PathVariable String dni) {
        try {
            return ResponseEntity.ok(alumnoService.buscarPorDni(dni));
        } catch (AlumnoException e) {
            return ResponseEntity.status(422).body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-2)
                            .mensaje("Error interno al buscar el alumno por DNI.")
                            .build()
            );
        }
    }

    @PostMapping("/{id}/reactivar")
    @Operation(summary = "Reactiva un alumno excluido por repetición",
            description = "Reactiva un alumno que fue excluido por exceder el límite de repeticiones. " +
                    "Elimina las calificaciones del último curso pero mantiene el historial de otros cursos.")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<AlumnoResponse> reactivarAlumno(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reactivacionService.reactivarAlumno(id));
        } catch (AlumnoException e) {
            return ResponseEntity.status(422).body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-2)
                            .mensaje("Error interno al reactivar el alumno: " + e.getMessage())
                            .build()
            );
        }
    }

    @PostMapping("/asignar-tutores")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    @Operation(summary = "Asigna múltiples tutores a un alumno",
        description = "Permite asignar uno o más tutores a un alumno específico. Requiere rol ADMIN o DIRECTOR.")
    public ResponseEntity<AlumnoResponse> asignarTutores(@RequestBody @Validated AsignarTutoresRequest request) {
        try {
            return ResponseEntity.ok(alumnoService.asignarTutores(request));
        } catch (AlumnoException e) {
            return ResponseEntity.badRequest().body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-2)
                            .mensaje("Error interno al asignar tutores: " + e.getMessage())
                            .build()
            );
        }
    }

    @DeleteMapping("/{alumnoId}/tutores/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
    @Operation(summary = "Remueve un tutor de un alumno",
        description = "Remueve un tutor específico de la lista de tutores de un alumno. Requiere rol ADMIN o DIRECTOR.")
    public ResponseEntity<AlumnoResponse> removerTutor(
            @PathVariable Long alumnoId, 
            @PathVariable Long tutorId) {
        try {
            return ResponseEntity.ok(alumnoService.removerTutor(alumnoId, tutorId));
        } catch (AlumnoException e) {
            return ResponseEntity.badRequest().body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-1)
                            .mensaje(e.getMessage())
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    AlumnoResponse.builder()
                            .alumno(null)
                            .code(-2)
                            .mensaje("Error interno al remover tutor: " + e.getMessage())
                            .build()
            );
        }
    }

}
