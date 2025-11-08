package com.grup14.luterano.examples;

/**
 * EJEMPLOS DE USO - MÚLTIPLES TUTORES POR ALUMNO
 * 
 * La funcionalidad permite que un alumno tenga asignados múltiples tutores.
 * Esto es útil para casos donde el alumno tiene padres separados, tutores legales múltiples, etc.
 * 
 * CAMBIOS REALIZADOS:
 * ===================
 * 
 * 1. ENTIDADES:
 *    - Alumno: Cambió @ManyToOne Tutor tutor -> @ManyToMany List<Tutor> tutores
 *    - Tutor: Agregó @ManyToMany(mappedBy = "tutores") List<Alumno> alumnos
 * 
 * 2. DTOs:
 *    - AlumnoDto: Cambió TutorDto tutor -> List<TutorDto> tutores
 *    - TutorDto: Agregó List<AlumnoDto> alumnos (para casos específicos)
 * 
 * 3. NUEVOS ENDPOINTS:
 *    - POST /alumno/asignar-tutores - Asigna múltiples tutores a un alumno
 *    - DELETE /alumno/{alumnoId}/tutores/{tutorId} - Remueve un tutor específico
 *    - POST /tutorAlumno/asignar-multiples-tutores - Asigna múltiples tutores (operación masiva)
 *    - POST /tutorAlumno/desasignar-multiples-tutores - Desasigna múltiples tutores (operación masiva)
 * 
 * EJEMPLOS DE JSON PARA REQUESTS:
 * ===============================
 * 
 * 1. Crear alumno con múltiples tutores:
 * POST /alumno/create
 * {
 *   "nombre": "Juan Carlos",
 *   "apellido": "Pérez",
 *   "dni": "12345678",
 *   "email": "juan.perez@email.com",
 *   "direccion": "Calle Falsa 123",
 *   "telefono": "+54 11 1234-5678",
 *   "genero": "MASCULINO",
 *   "tipoDoc": "DNI",
 *   "fechaNacimiento": "2010-03-15",
 *   "fechaIngreso": "2025-01-01",
 *   "cursoActual": {
 *     "id": 1
 *   },
 *   "tutores": [
 *     {
 *       "id": 1
 *     },
 *     {
 *       "id": 2
 *     }
 *   ]
 * }
 * 
 * 2. Asignar tutores a alumno existente:
 * POST /alumno/asignar-tutores
 * {
 *   "alumnoId": 5,
 *   "tutorIds": [1, 2, 3]
 * }
 * 
 * 3. Remover un tutor específico:
 * DELETE /alumno/5/tutores/2
 * 
 * 4. Asignar múltiples tutores (operación masiva):
 * POST /tutorAlumno/asignar-multiples-tutores
 * {
 *   "alumnoId": 5,
 *   "tutorIds": [1, 3, 4]
 * }
 * 
 * 5. Desasignar múltiples tutores (operación masiva):
 * POST /tutorAlumno/desasignar-multiples-tutores
 * {
 *   "alumnoId": 5,
 *   "tutorIds": [2, 3]
 * }
 * 
 * ESTRUCTURA DE BASE DE DATOS:
 * ============================
 * 
 * Se crea automáticamente una tabla intermedia:
 * 
 * tabla: alumno_tutor
 * - alumno_id (FK -> alumno.id)
 * - tutor_id (FK -> tutor.id)
 * 
 * CASOS DE USO:
 * =============
 * 
 * 1. Padres separados: Cada padre puede ser un tutor independiente
 * 2. Tutores legales múltiples: Abuelos, tíos, etc.
 * 3. Familias ensambladas: Padrastros/madrastras como tutores adicionales
 * 4. Situaciones legales especiales: Tutores designados por el juzgado
 * 5. Asignación masiva: Asignar múltiples tutores de una vez (ej: cambio de tutelaje)
 * 6. Desasignación masiva: Remover varios tutores simultáneamente
 * 
 * OPERACIONES MASIVAS:
 * ===================
 * 
 * Las operaciones masivas permiten:
 * - Asignar varios tutores de una vez con un solo request
 * - Desasignar varios tutores simultáneamente
 * - Recibir feedback detallado sobre qué tutores se procesaron exitosamente
 * - Manejar casos donde algunos tutores ya están asignados/no asignados
 * 
 * Beneficios:
 * - Reduce el número de llamadas HTTP
 * - Operación atómica (todo o nada)
 * - Mejor experiencia para el usuario en casos complejos
 * 
 * VALIDACIONES IMPLEMENTADAS:
 * ==========================
 * 
 * - El alumno debe existir antes de asignar tutores
 * - Todos los tutores especificados deben existir en la base de datos
 * - No se pueden duplicar tutores para un mismo alumno (manejado por la relación ManyToMany)
 * - Al remover un tutor, se valida que esté asignado al alumno
 * 
 * PERMISOS REQUERIDOS:
 * ===================
 * 
 * - Crear alumno con tutores: ADMIN, DIRECTOR
 * - Asignar tutores (individual): ADMIN, DIRECTOR, PRECEPTOR
 * - Remover tutores (individual): ADMIN, DIRECTOR, PRECEPTOR
 * - Asignar tutores (masivo): ADMIN, DIRECTOR
 * - Desasignar tutores (masivo): ADMIN, DIRECTOR
 * - Consultar alumnos con tutores: ADMIN, DIRECTOR, PRECEPTOR
 * - Listar alumnos a cargo de un tutor: ADMIN, DIRECTOR, PRECEPTOR
 * 
 * RESPUESTA TÍPICA:
 * ================
 * 
 * Respuesta normal:
 * {
 *   "alumno": {
 *     "id": 5,
 *     "nombre": "Juan Carlos",
 *     "apellido": "Pérez",
 *     "dni": "12345678",
 *     "cursoActual": {
 *       "id": 1,
 *       "nombre": "1° A"
 *     },
 *     "tutores": [
 *       {
 *         "id": 1,
 *         "nombre": "María",
 *         "apellido": "González",
 *         "dni": "87654321"
 *       },
 *       {
 *         "id": 2,
 *         "nombre": "Pedro",
 *         "apellido": "Pérez",
 *         "dni": "11223344"
 *       }
 *     ]
 *   },
 *   "code": 0,
 *   "mensaje": "Tutores asignados correctamente"
 * }
 * 
 * Respuesta operación masiva con algunos tutores ya asignados:
 * {
 *   "alumno": { ... },
 *   "code": 0,
 *   "mensaje": "Se asignaron 2 tutores correctamente. Los siguientes tutores ya estaban asignados: María González"
 * }
 * 
 * Respuesta desasignación masiva:
 * {
 *   "alumno": { ... },
 *   "code": 0,
 *   "mensaje": "Se desasignaron 2 tutores correctamente. Los siguientes tutores no estaban asignados: Carlos Ruiz"
 * }
 */
public class EjemploMultiplesTutores {
    // Esta clase solo contiene documentación de ejemplo
}