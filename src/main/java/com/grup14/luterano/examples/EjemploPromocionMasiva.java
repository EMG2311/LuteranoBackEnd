package com.grup14.luterano.examples;

/**
 * SISTEMA DE PROMOCIÓN MASIVA DE ALUMNOS
 * =====================================
 * 
 * Este sistema permite procesar automáticamente a todos los alumnos del colegio
 * aplicando las reglas de promoción al final del año lectivo.
 * 
 * 🎯 REGLAS DE PROMOCIÓN:
 * ----------------------
 * 1. MENOS DE 3 MATERIAS DESAPROBADAS: El alumno promociona al curso siguiente
 *    - 4° A → 5° A
 *    - 3° B → 4° B
 * 
 * 2. 3 O MÁS MATERIAS DESAPROBADAS: El alumno repite año
 *    - Se incrementa el contador de repeticiones
 *    - Se valida que no exceda el límite máximo
 * 
 * 3. 6TO AÑO: El alumno egresa automáticamente
 *    - Estado cambia a EGRESADO
 *    - Se excluye de consultas futuras
 * 
 * 4. LÍMITE DE REPETICIONES EXCEDIDO: El alumno se excluye del sistema
 *    - Estado cambia a EXCLUIDO_POR_REPETICION
 *    - Se quita del curso actual
 *    - Se excluye de todas las consultas como los egresados
 *    - ⚠️ REQUIERE REACTIVACIÓN MANUAL para volver al sistema
 * 
 * 5. LÍMITE DE REPETICIONES: Configurable (default: 2)
 *    - Si excede el límite, se excluye automáticamente
 * 
 * 🔧 ENDPOINTS DISPONIBLES:
 * -------------------------
 * 
 * POST /promocion/masiva
 * ► Ejecuta la promoción masiva real
 * ► Requiere permisos de ADMIN o DIRECTOR
 * 
 * POST /promocion/masiva/simulacion
 * ► Simula la promoción sin hacer cambios
 * ► Permite ver resultados antes de ejecutar
 * ► Accesible para PRECEPTOR también
 * 
 * 🔄 ENDPOINTS DE REACTIVACIÓN:
 * -----------------------------
 * 
 * POST /alumno/{id}/reactivar
 * ► Reactiva un alumno excluido por repetición
 * ► Borra las calificaciones del último curso
 * ► Mantiene historial de materias de otros cursos
 * ► Cambia estado a REGULAR y resetea repeticiones
 * ► Requiere permisos de ADMIN o DIRECTOR
 * 
 * GET /alumno/excluidos
 * ► Lista alumnos excluidos por repetición
 * ► Para identificar candidatos a reactivación
 * 
 * 📋 EJEMPLO DE REQUEST:
 * ---------------------
 * {
 *   "anio": 2025,
 *   "cicloLectivoId": 1,
 *   "procesarEgresados": true,
 *   "maxRepeticiones": 2,
 *   "dryRun": false
 * }
 * 
 * 📊 EJEMPLO DE RESPONSE:
 * ----------------------
 * {
 *   "procesados": 150,
 *   "promocionados": 120,
 *   "repitentes": 25,
 *   "egresados": 5,
 *   "excluidos": 3,
 *   "noProcesados": 0,
 *   "dryRun": false,
 *   "resumen": [
 *     {
 *       "alumnoId": 1,
 *       "dni": "12345678",
 *       "apellido": "Pérez",
 *       "nombre": "Juan",
 *       "cursoAnterior": "4° A",
 *       "cursoNuevo": "5° A",
 *       "accion": "PROMOCIONADO",
 *       "materiasDesaprobadas": 2,
 *       "repeticionesActuales": 0
 *     },
 *     {
 *       "alumnoId": 2,
 *       "dni": "87654321",
 *       "apellido": "González",
 *       "nombre": "María",
 *       "cursoAnterior": "3° B",
 *       "cursoNuevo": "3° B (Repite)",
 *       "accion": "REPITENTE",
 *       "materiasDesaprobadas": 4,
 *       "repeticionesActuales": 1
 *     },
 *     {
 *       "alumnoId": 3,
 *       "dni": "11223344",
 *       "apellido": "Rodríguez",
 *       "nombre": "Carlos",
 *       "cursoAnterior": "2° A",
 *       "cursoNuevo": "EXCLUIDO",
 *       "accion": "EXCLUIDO_POR_REPETICION",
 *       "motivo": "Excede límite de repeticiones (2)",
 *       "materiasDesaprobadas": 5,
 *       "repeticionesActuales": 2
 *     }
 *   ],
 *   "code": 0,
 *   "mensaje": "Promoción masiva ejecutada. Total: 150 alumnos"
 * }
 * 
 * 🚫 ALUMNOS EGRESADOS Y EXCLUIDOS:
 * ----------------------------------
 * - Los alumnos con estado EGRESADO o EXCLUIDO_POR_REPETICION se excluyen automáticamente de:
 *   ✅ Listados de alumnos activos
 *   ✅ Reportes de notas
 *   ✅ Asignación a mesas de examen
 *   ✅ Consultas de asistencia
 *   ✅ Promoción masiva (no se procesan)
 * 
 * - Endpoints específicos para consultar excluidos:
 *   📋 GET /alumno/egresados - Solo ADMIN/DIRECTOR
 *   📋 GET /alumno/excluidos - Solo ADMIN/DIRECTOR
 * 
 * 🔄 PROCESO DE REACTIVACIÓN:
 * ---------------------------
 * 1. Identificar alumno excluido: GET /alumno/excluidos
 * 2. Evaluar caso individualmente
 * 3. Reactivar si procede: POST /alumno/{id}/reactivar
 * 4. El alumno vuelve al mismo curso sin las calificaciones del período que causó la exclusión
 * 5. Mantiene historial de materias de otros cursos/años
 * 6. Contador de repeticiones se resetea a 0
 * 
 * 🔄 PROCESO RECOMENDADO:
 * ----------------------
 * 1. Ejecutar simulación primero: POST /promocion/masiva/simulacion
 * 2. Revisar resultados y validar casos especiales
 * 3. Revisar alumnos que serían excluidos por repetición
 * 4. Ejecutar promoción real: POST /promocion/masiva
 * 5. Revisar lista de excluidos: GET /alumno/excluidos
 * 6. Evaluar casos de reactivación individuales
 * 7. Reactivar si corresponde: POST /alumno/{id}/reactivar
 * 8. Verificar reportes post-promoción
 * 
 * ⚠️ CONSIDERACIONES IMPORTANTES:
 * ------------------------------
 * - El proceso es irreversible una vez ejecutado (sin dryRun)
 * - Se basa en NotaFinalService para determinar materias aprobadas/desaprobadas
 * - Los historiales de curso se cierran automáticamente
 * - Los alumnos promocionados necesitarán nuevos historiales para el próximo ciclo
 * 
 * 📝 CAMPOS AGREGADOS A ALUMNO:
 * ----------------------------
 * - cantidadRepeticiones: Integer (default: 0)
 * - maxRepeticionesPermitidas: Integer (default: 2)
 * 
 * Los filtros de egresados ya están implementados en:
 * - AlumnoService.listAlumnos()
 * - AlumnoSpecification.alumnosActivos()
 * - Reportes principales (por especificación anterior)
 */
public class EjemploPromocionMasiva {
    
    // Esta clase es solo documentativa
    // Los endpoints están implementados en PromocionMasivaController
    
}