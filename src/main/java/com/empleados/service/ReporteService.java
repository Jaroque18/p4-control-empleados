package com.empleados.service;

import com.empleados.model.ReporteEmpleado;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de generación de reportes.
 * 
 * Define el contrato para:
 * - Calcular horas trabajadas por empleado en un periodo
 * - Generar reportes en formato DTO
 * - Exportar reportes a Excel
 * 
 * Solo accesible por administradores.
 */

public interface ReporteService {

    /**
     * Genera el reporte de horas trabajadas para todos los empleados en un mes específico.
     * 
     * Proceso:
     * 1. Filtra los registros de horarios del mes seleccionado
     * 2. Agrupa por empleado
     * 3. Para cada empleado:
     *    - Suma las horas trabajadas (diferencia entre salida y entrada)
     *    - Cuenta los días distintos trabajados
     * 4. Devuelve una lista de DTOs ordenada por nombre de empleado
     * 
     * @param anio Año del reporte (ej: 2026)
     * @param mes Mes del reporte (1-12)
     * @return Lista de ReporteEmpleado, uno por cada empleado que trabajo en ese mes.
     *         Lista vacia si no hay registros.
     */
    List<ReporteEmpleado> generarReporteMensual(int anio, int mes);
    
    /**
     * Genera un archivo Excel (.xlsx) con el reporte de horas trabajadas.
     * 
     * El archivo contiene:
     * - Encabezado con titulo y periodo del reporte
     * - Tabla con columnas: ID, Cédula, Nombre, Correo, Horas Trabajadas, Días Trabajados
     * - Formato con estilos (encabezados en negrita, bordes, etc.)
     * 
     * Utiliza Apache POI para la generacion del archivo.
     * 
     * @param anio Año del reporte
     * @param mes Mes del reporte
     * @return Array de bytes del archivo Excel generado, listo para ser descargado
     * @throws Exception Si ocurre un error durante la generacion del archivo
     */
    byte[] generarExcel(int anio, int mes) throws Exception;

    byte[] generarCSV(int anio, int mes);
}