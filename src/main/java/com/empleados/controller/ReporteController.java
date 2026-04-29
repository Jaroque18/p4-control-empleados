package com.empleados.controller;

import com.empleados.model.ReporteEmpleado;
import com.empleados.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador para la gestión de reportes de horas trabajadas.
 * 
 * Responsabilidades:
 * - Mostrar interfaz de selección de período (vista con filtro de mes/año)
 * - Generar y mostrar reporte en formato HTML
 * - Generar y descargar reporte en formato Excel
 * 
 * Rutas:
 * - GET  /admin/reportes           → Vista con filtros y tabla de resultados
 * - GET  /admin/reportes/descargar → Descarga del archivo Excel
 * 
 * Seguridad:
 * - Todas las rutas bajo /admin/** requieren rol ADMIN (definido en SecurityConfig)
 * - Solo administradores pueden acceder a esta funcionalidad
 * 
 * @Controller: Marca esta clase como controlador MVC de Spring.
 *              Puede devolver nombres de vistas (Thymeleaf) o ResponseEntity.
 * 
 * @Autowired: Inyección automática del servicio ReporteService.
 *             Spring encuentra la implementación (ReporteServiceImpl) y la inyecta.
 */
@Controller
@RequestMapping("/admin/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    /**
     * Muestra la vista principal de reportes.
     * 
     * Funcionalidad:
     * - Si no hay parámetros (primera carga): muestra solo el formulario de filtros
     * - Si hay parámetros (año y mes): genera el reporte y lo muestra en tabla
     * 
     * Flujo:
     * 1. Recibe año y mes como parámetros opcionales
     * 2. Si están presentes, invoca al servicio para generar el reporte
     * 3. Pasa los datos a la vista (Thymeleaf)
     * 4. La vista renderiza el formulario + tabla de resultados
     * 
     * @param anio Año del reporte (opcional, ej: 2026)
     * @param mes  Mes del reporte (opcional, 1-12)
     * @param model Objeto Model de Spring MVC para pasar datos a la vista
     * @return Nombre de la plantilla Thymeleaf (admin/reportes.html)
     */
    @GetMapping
    public String mostrarReportes(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) Integer mes,
            Model model) {

        // Si se proporcionaron filtros, generar el reporte
        if (anio != null && mes != null) {
            List<ReporteEmpleado> reporte = reporteService.generarReporteMensual(anio, mes);
            model.addAttribute("reporte", reporte);
            model.addAttribute("anioFiltro", anio);
            model.addAttribute("mesFiltro", mes);
        } else {
            // Primera carga: sugerir mes/año actual como valores por defecto
            LocalDate hoy = LocalDate.now();
            model.addAttribute("anioActual", hoy.getYear());
            model.addAttribute("mesActual", hoy.getMonthValue());
        }

        return "admin/reportes";
    }

    /**
     * Descarga el reporte en formato Excel (.xlsx).
     * 
     * Proceso:
     * 1. Recibe año y mes como parámetros obligatorios
     * 2. Invoca al servicio para generar el archivo Excel
     * 3. Configura las cabeceras HTTP para forzar la descarga
     * 4. Devuelve el archivo como array de bytes
     * 
     * Cabeceras HTTP:
     * - Content-Disposition: attachment → fuerza la descarga en lugar de mostrar en navegador
     * - Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
     *   → indica que es un archivo Excel moderno (.xlsx)
     * 
     * @param anio Año del reporte (obligatorio)
     * @param mes  Mes del reporte (obligatorio)
     * @return ResponseEntity con el archivo Excel como recurso descargable
     * @throws Exception Si ocurre un error durante la generación del archivo
     */
    @GetMapping("/descargar")
    public ResponseEntity<ByteArrayResource> descargarExcel(
            @RequestParam int anio,
            @RequestParam int mes) throws Exception {

        // Generar archivo Excel
        byte[] excelBytes = reporteService.generarExcel(anio, mes);

        // Nombre del archivo descargado (ej: reporte_2026_03.xlsx)
        String nombreArchivo = String.format("reporte_%d_%02d.xlsx", anio, mes);

        // Configurar cabeceras HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo);

        // Devolver el archivo como recurso descargable
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelBytes.length)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excelBytes));
    }

    /**
     * Descarga el reporte en formato CSV (.csv).
     * * Proceso:
     * 1. Invoca al servicio para generar el contenido CSV en bytes.
     * 2. Configura el Content-Type como "text/csv".
     * 3. Configura el nombre del archivo con extensión .csv.
     * * @param anio Año del reporte
     * @param mes  Mes del reporte
     * @return ResponseEntity con el archivo CSV
     */
    @GetMapping("/descargar/csv")
    public ResponseEntity<ByteArrayResource> descargarCSV(
            @RequestParam int anio,
            @RequestParam int mes) {

        // Generar contenido CSV
        byte[] csvBytes = reporteService.generarCSV(anio, mes);

        // Nombre del archivo (ej: reporte_2026_03.csv)
        String nombreArchivo = String.format("reporte_%d_%02d.csv", anio, mes);

        // Configurar cabeceras
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(csvBytes.length)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(csvBytes));
    }
}