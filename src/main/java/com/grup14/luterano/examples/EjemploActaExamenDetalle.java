package com.grup14.luterano.examples;

/**
 * Ejemplo de uso del endpoint GET /actas/{id} para obtener el acta detallada.
 * 
 * El endpoint ahora devuelve un DTO completo con toda la información necesaria
 * para imprimir el acta, incluyendo:
 * 
 * ENCABEZADO:
 * - id, numeroActa, fechaCierre, cerrada, observaciones
 * - Información del curso: año, división, nivel
 * - Información de la materia: id y nombre
 * - Información del turno: id, nombre y fecha del examen
 * 
 * DETALLE DE DOCENTES:
 * - Array con docentes a cargo de la mesa
 * - Cada docente incluye: id, nombreCompleto, dni
 * 
 * DETALLE DE ALUMNOS:
 * - Array con todos los alumnos convocados
 * - Cada alumno incluye: alumnoId, apellido, nombre, dni
 * - Nota final obtenida en la mesa
 * - Observación: "Aprobado" (nota >= 6), "Desaprobado" (nota < 6) o "Ausente"
 * 
 * EJEMPLO DE RESPUESTA JSON:
 * {
 *   "code": 1,
 *   "mensaje": "OK",
 *   "acta": {
 *     "id": 123,
 *     "numeroActa": "ACTA-2025-001",
 *     "fechaCierre": "2025-12-01",
 *     "cerrada": true,
 *     "observaciones": "Sin novedades",
 *     "cursoAnio": 5,
 *     "cursoDivision": "B",
 *     "cursoNivel": "Secundaria",
 *     "materiaId": 44,
 *     "materiaNombre": "Matemática",
 *     "turnoId": 7,
 *     "turnoNombre": "Diciembre 2025",
 *     "fecha": "2025-12-01",
 *     "docentes": [
 *       {
 *         "id": 10,
 *         "nombreCompleto": "Suárez, Romina",
 *         "dni": "25.123.456"
 *       }
 *     ],
 *     "alumnos": [
 *       {
 *         "alumnoId": 555,
 *         "apellido": "Pérez",
 *         "nombre": "Ana",
 *         "dni": "45.678.901",
 *         "nota": 8,
 *         "observacion": "Aprobado"
 *       },
 *       {
 *         "alumnoId": 556,
 *         "apellido": "López",
 *         "nombre": "Juan", 
 *         "dni": "44.112.223",
 *         "nota": 4,
 *         "observacion": "Desaprobado"
 *       },
 *       {
 *         "alumnoId": 557,
 *         "apellido": "Marín",
 *         "nombre": "Sofía",
 *         "dni": "48.111.222", 
 *         "nota": null,
 *         "observacion": "Ausente"
 *       }
 *     ]
 *   }
 * }
 * 
 * POSTMAN:
 * GET http://localhost:8080/actas/123
 * Authorization: Bearer {token}
 * 
 * CONSIDERACIONES TÉCNICAS:
 * - El endpoint usa fetch joins para evitar N+1 queries
 * - Los docentes se ordenan automáticamente
 * - Los alumnos incluyen su estado de convocatoria
 * - La fecha del examen se toma de mesa.fecha, o fechaCierre si no existe
 * 
 * REGLAS DE NEGOCIO PARA OBSERVACIÓN:
 * - Si estado = AUSENTE → "Ausente"
 * - Si nota >= 6 → "Aprobado"  
 * - Si nota < 6 → "Desaprobado"
 * - Si nota = null → "Ausente"
 */
public class EjemploActaExamenDetalle {
    
    // Este endpoint reemplaza el anterior que solo devolvía información básica
    // Ahora es completo y listo para impresión/visualización en frontend
    
}