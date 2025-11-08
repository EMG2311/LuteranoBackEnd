package com.grup14.luterano.examples;

/**
 * Ejemplo del proceso de promoción con creación automática del ciclo lectivo siguiente.
 * 
 * FUNCIONALIDAD IMPLEMENTADA:
 * Cuando se ejecuta la promoción masiva de alumnos, el sistema ahora maneja automáticamente
 * los ciclos lectivos, creando el del año siguiente si no existe.
 * 
 * PROCESO DE PROMOCIÓN:
 * 
 * 1. ALUMNOS QUE PROMOCIONAN (< 3 materias desaprobadas):
 *    - Cierra el historial curso actual en el ciclo lectivo actual
 *    - Busca el ciclo lectivo del año siguiente
 *    - Si no existe, lo crea automáticamente con:
 *      * nombre: "Ciclo Lectivo {año+1}"
 *      * fechaDesde: 1 enero del año siguiente
 *      * fechaHasta: 31 diciembre del año siguiente
 *    - Crea un nuevo historial curso en el ciclo siguiente con el curso superior
 * 
 * 2. ALUMNOS QUE REPITEN (≥ 3 materias desaprobadas):
 *    - Cierra el historial curso actual en el ciclo lectivo actual
 *    - Obtiene o crea el ciclo lectivo del año siguiente (igual que promoción)
 *    - Crea un nuevo historial curso en el ciclo siguiente con el mismo curso
 *    - Incrementa el contador de repeticiones
 * 
 * 3. ALUMNOS QUE EGRESAN (6to año con < 3 materias desaprobadas):
 *    - Cierra el historial curso actual
 *    - Cambia estado a EGRESADO
 *    - No necesita ciclo lectivo siguiente
 * 
 * EJEMPLO DE FLUJO:
 * Ciclo actual: "Ciclo Lectivo 2025" (01/01/2025 - 31/12/2025)
 * 
 * Al promocionar alumnos:
 * - Si no existe "Ciclo Lectivo 2026", lo crea automáticamente
 * - Alumnos de 1°A que promocionan → van a 2°A en ciclo 2026
 * - Alumnos de 1°A que repiten → quedan en 1°A pero en ciclo 2026
 * 
 * VENTAJAS:
 * - No es necesario crear manualmente el ciclo lectivo siguiente
 * - Garantiza la continuidad del historial académico
 * - Separa correctamente los datos por año lectivo
 * - Permite consultas precisas por ciclo/año
 * 
 * EJEMPLO DE REQUEST DE PROMOCIÓN:
 * POST /promocion-masiva/ejecutar
 * {
 *   "anio": 2025,
 *   "cicloLectivoId": 5,  // Ciclo Lectivo 2025
 *   "maxRepeticiones": 2,
 *   "dryRun": false
 * }
 * 
 * RESULTADO:
 * - Si no existía "Ciclo Lectivo 2026", se crea automáticamente
 * - Los alumnos promocionados/repitentes pasan al ciclo 2026
 * - Se mantiene la trazabilidad: historial 2025 (cerrado) → historial 2026 (activo)
 * 
 * CONSIDERACIONES TÉCNICAS:
 * - El método `obtenerOcrearCicloLectivoSiguiente()` es idempotente
 * - Si ya existe el ciclo siguiente, simplemente lo usa
 * - Los logs registran cuando se crea un nuevo ciclo
 * - La transacción garantiza consistencia entre historial y ciclo
 * 
 * QUERIES ÚTILES DESPUÉS DE LA PROMOCIÓN:
 * - Alumnos activos en ciclo 2026: WHERE historialCurso.fechaHasta IS NULL AND cicloLectivo.nombre = 'Ciclo Lectivo 2026'
 * - Historial completo de un alumno: SELECT * FROM historial_curso WHERE alumno_id = X ORDER BY ciclo_lectivo_id
 * - Promociones del año: WHERE cicloLectivo.nombre = 'Ciclo Lectivo 2026' AND fechaDesde = fecha_promocion
 */
public class EjemploPromocionConCicloLectivo {

    // La promoción ahora maneja automáticamente los ciclos lectivos
    // Los alumnos siempre pasan al año lectivo siguiente, no al mismo año
    
}