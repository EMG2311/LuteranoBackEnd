package com.grup14.luterano.examples;

/**
 * EJEMPLOS DE AUTENTICACIÓN CON ENTIDADES ESPECÍFICAS POR ROL
 * 
 * El sistema de autenticación ahora incluye automáticamente en el token JWT 
 * el ID de la entidad específica correspondiente al rol del usuario.
 * 
 * ESTRUCTURA DEL TOKEN JWT:
 * ========================
 * 
 * El token JWT incluye estos claims adicionales según el rol:
 * 
 * Para ROLE_DOCENTE:
 * {
 *   "role": "ROLE_DOCENTE",
 *   "userId": 1,
 *   "docenteId": 123
 * }
 * 
 * Para ROLE_PRECEPTOR:
 * {
 *   "role": "ROLE_PRECEPTOR", 
 *   "userId": 1,
 *   "preceptorId": 456
 * }
 * 
 * Para ROLE_ALUMNO:
 * {
 *   "role": "ROLE_ALUMNO",
 *   "userId": 1,
 *   "alumnoId": 789
 * }
 * 
 * Para ROLE_TUTOR:
 * {
 *   "role": "ROLE_TUTOR",
 *   "userId": 1,
 *   "tutorId": 321
 * }
 * 
 * Para ROLE_ADMIN, ROLE_DIRECTOR, ROLE_AUXILIAR:
 * {
 *   "role": "ROLE_ADMIN",
 *   "userId": 1
 *   // No se agrega ID adicional ya que no tienen entidad específica
 * }
 * 
 * CÓMO USAR EN EL FRONTEND:
 * ========================
 * 
 * 1. Decodificar el token JWT para obtener los claims
 * 2. Verificar el rol del usuario
 * 3. Usar el ID específico según corresponda:
 * 
 * // Ejemplo en JavaScript
 * const tokenPayload = jwt.decode(token);
 * const userRole = tokenPayload.role;
 * 
 * switch(userRole) {
 *   case 'ROLE_DOCENTE':
 *     const docenteId = tokenPayload.docenteId;
 *     // Usar docenteId para llamadas específicas de docente
 *     break;
 *   case 'ROLE_PRECEPTOR':
 *     const preceptorId = tokenPayload.preceptorId;
 *     // Usar preceptorId para funciones de preceptor
 *     break;
 *   case 'ROLE_ALUMNO':
 *     const alumnoId = tokenPayload.alumnoId;
 *     // Usar alumnoId para perfil de alumno
 *     break;
 *   case 'ROLE_TUTOR':
 *     const tutorId = tokenPayload.tutorId;
 *     // Usar tutorId para gestión de tutores
 *     break;
 * }
 * 
 * CASOS DE USO:
 * =============
 * 
 * 1. **Docente**: Al loguearse, obtiene automáticamente su docenteId para:
 *    - Ver sus materias asignadas
 *    - Gestionar calificaciones de sus alumnos
 *    - Acceder a su perfil de docente
 * 
 * 2. **Preceptor**: Al loguearse, obtiene su preceptorId para:
 *    - Gestionar los cursos bajo su responsabilidad
 *    - Ver alumnos de sus cursos
 *    - Manejar asistencias y tardanzas
 * 
 * 3. **Alumno**: Al loguearse, obtiene su alumnoId para:
 *    - Ver su perfil académico
 *    - Consultar sus calificaciones
 *    - Acceder a su historial académico
 * 
 * 4. **Tutor**: Al loguearse, obtiene su tutorId para:
 *    - Ver información de los alumnos a su cargo
 *    - Acceder a reportes de sus tutorados
 *    - Gestionar comunicaciones con la institución
 * 
 * VENTAJAS DEL SISTEMA:
 * ====================
 * 
 * 1. **Seguridad**: Solo el usuario autenticado puede acceder a su propia información
 * 2. **Eficiencia**: No necesita consultas adicionales para obtener el ID de la entidad
 * 3. **Simplicidad**: El frontend tiene acceso inmediato a la información necesaria
 * 4. **Escalabilidad**: Fácil de extender para nuevos roles
 * 5. **Token Optimizado**: Solo incluye los IDs esenciales, no objetos completos
 * 6. **Menor Tamaño**: El token es más liviano al no incluir información completa del usuario
 * 
 * EJEMPLO DE RESPUESTA DE LOGIN:
 * ==============================
 * 
 * POST /auth/authenticate
 * {
 *   "email": "docente@school.edu",
 *   "password": "password123"
 * }
 * 
 * Respuesta:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "mensaje": "hola Juan Pérez",
 *   "code": 0
 * }
 * 
 * Al decodificar el token se obtiene:
 * {
 *   "role": "ROLE_DOCENTE",
 *   "userId": 1,
 *   "docenteId": 15,
 *   "exp": 1699123456,
 *   "iat": 1699036056
 * }
 */
public class EjemploAutenticacionPorRol {
    // Esta clase solo contiene documentación de ejemplo
}