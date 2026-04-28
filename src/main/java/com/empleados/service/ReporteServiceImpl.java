package com.empleados.service;

import com.empleados.model.Horario;
import com.empleados.model.ReporteEmpleado;
import com.empleados.repository.HorarioRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de generación de reportes de horas trabajadas.
 * 
 * Esta clase contiene la lógica de negocio para:
 * - Calcular horas trabajadas a partir de registros de entrada/salida
 * - Agrupar información por empleado
 * - Generar archivos Excel con formato profesional
 * 
 * @Service: Marca esta clase como un componente de servicio de Spring.
 *           Spring la detecta automáticamente y la registra en el contenedor IoC.
 * 
 * @Autowired: Inyección de dependencias automática por Spring.
 *             Spring busca un bean del tipo HorarioRepository y lo inyecta.
 *             Evita el acoplamiento fuerte y facilita el testing (se puede inyectar un mock).
 */
@Service
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private HorarioRepository horarioRepository;

    /**
     * Genera el reporte mensual de horas trabajadas por empleado.
     * 
     * Algoritmo:
     * 1. Calcula el rango de fechas del mes (primer día 00:00 - último día 23:59)
     * 2. Consulta todos los horarios del mes usando HorarioRepository
     * 3. Filtra solo registros completos (con salida registrada)
     * 4. Agrupa por empleado
     * 5. Para cada empleado:
     *    - Calcula total de horas: suma de (horaSalida - horaEntrada) convertida a horas
     *    - Cuenta días distintos trabajados
     * 6. Ordena alfabéticamente por nombre
     */
    @Override
    public List<ReporteEmpleado> generarReporteMensual(int anio, int mes) {
        
        // Calcular rango de fechas del mes
        LocalDate primerDia = LocalDate.of(anio, mes, 1);
        LocalDate ultimoDia = primerDia.withDayOfMonth(primerDia.lengthOfMonth());
        
        LocalDateTime inicio = primerDia.atStartOfDay();
        LocalDateTime fin    = ultimoDia.atTime(LocalTime.MAX);

        // Obtener todos los horarios del mes (sin filtros de usuario ni estado)
        List<Horario> horarios = horarioRepository.findAllConFiltros(null, inicio, fin, null);

        // Filtrar solo registros completos (con salida) y agrupar por empleado
        Map<Integer, List<Horario>> horariosPorEmpleado = horarios.stream()
                .filter(h -> h.getHoraSalida() != null)  // Solo registros completos
                .collect(Collectors.groupingBy(Horario::getIdUsuario));

        // Generar DTOs
        List<ReporteEmpleado> reporte = new ArrayList<>();
        
        for (Map.Entry<Integer, List<Horario>> entry : horariosPorEmpleado.entrySet()) {
            List<Horario> horariosEmpleado = entry.getValue();
            
            if (horariosEmpleado.isEmpty()) continue;

            // Tomar datos del primer registro ya que todos pertenecen al mismo empleado
            Horario primero = horariosEmpleado.get(0);

            // Calcular horas trabajadas: suma de diferencias entre salida y entrada
            double totalHoras = horariosEmpleado.stream()
                    .mapToDouble(h -> {
                        Duration duracion = Duration.between(h.getHoraEntrada(), h.getHoraSalida());
                        return duracion.toMinutes() / 60.0;  // Convertir minutos a horas decimales
                    })
                    .sum();

            // Contar días distintos trabajados
            long diasTrabajados = horariosEmpleado.stream()
                    .map(h -> h.getHoraEntrada().toLocalDate())
                    .distinct()
                    .count();

            ReporteEmpleado dto = new ReporteEmpleado(
                    primero.getUsuario().getIdUsuario(),
                    primero.getUsuario().getCedula(),
                    primero.getUsuario().getNombre(),
                    primero.getUsuario().getCorreo(),
                    Math.round(totalHoras * 100.0) / 100.0,  // Redondear a 2 decimales
                    (int) diasTrabajados
            );

            reporte.add(dto);
        }

        // Ordenar alfabeticamente por nombre
        reporte.sort(Comparator.comparing(ReporteEmpleado::getNombre));
        
        return reporte;
    }

    /**
     * Genera un archivo Excel (.xlsx) con el reporte de horas trabajadas.
     * 
     * Utiliza Apache POI para crear un libro de Excel con:
     * - Hoja "Reporte Mensual"
     * - Encabezado con título y período
     * - Tabla con datos de empleados
     * - Estilos profesionales (bordes, negritas, alineación)
     * 
     * @throws Exception Si ocurre algun error durante la generacion como falta de memoria
     */
    @Override
    public byte[] generarExcel(int anio, int mes) throws Exception {
        
        List<ReporteEmpleado> datos = generarReporteMensual(anio, mes);

        // Crear libro de trabajo Excel
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Reporte Mensual");

        
        // Estilo para título
        CellStyle estiloTitulo = workbook.createCellStyle();
        Font fuenteTitulo = workbook.createFont();
        fuenteTitulo.setBold(true);
        fuenteTitulo.setFontHeightInPoints((short) 14);
        estiloTitulo.setFont(fuenteTitulo);
        estiloTitulo.setAlignment(HorizontalAlignment.CENTER);

        // Estilo para encabezados de columna
        CellStyle estiloEncabezado = workbook.createCellStyle();
        Font fuenteEncabezado = workbook.createFont();
        fuenteEncabezado.setBold(true);
        estiloEncabezado.setFont(fuenteEncabezado);
        estiloEncabezado.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloEncabezado.setBorderBottom(BorderStyle.THIN);
        estiloEncabezado.setBorderTop(BorderStyle.THIN);
        estiloEncabezado.setBorderLeft(BorderStyle.THIN);
        estiloEncabezado.setBorderRight(BorderStyle.THIN);

        // Estilo para celdas de datos
        CellStyle estiloDatos = workbook.createCellStyle();
        estiloDatos.setBorderBottom(BorderStyle.THIN);
        estiloDatos.setBorderTop(BorderStyle.THIN);
        estiloDatos.setBorderLeft(BorderStyle.THIN);
        estiloDatos.setBorderRight(BorderStyle.THIN);


        int filaActual = 0;

        // Fila 0: Título
        Row filaTitulo = sheet.createRow(filaActual++);
        Cell celdaTitulo = filaTitulo.createCell(0);
        celdaTitulo.setCellValue("REPORTE DE HORAS TRABAJADAS - " + 
                getNombreMes(mes) + " " + anio);
        celdaTitulo.setCellStyle(estiloTitulo);

        // Fila en blanco
        filaActual++;

        // Fila de encabezados
        Row filaEncabezados = sheet.createRow(filaActual++);
        String[] encabezados = {"ID", "Cédula", "Nombre", "Correo", "Horas Trabajadas", "Días Trabajados"};
        
        for (int i = 0; i < encabezados.length; i++) {
            Cell celda = filaEncabezados.createCell(i);
            celda.setCellValue(encabezados[i]);
            celda.setCellStyle(estiloEncabezado);
        }

        // Filas de datos
        for (ReporteEmpleado dto : datos) {
            Row fila = sheet.createRow(filaActual++);
            
            fila.createCell(0).setCellValue(dto.getIdUsuario());
            fila.createCell(1).setCellValue(dto.getCedula());
            fila.createCell(2).setCellValue(dto.getNombre());
            fila.createCell(3).setCellValue(dto.getCorreo());
            fila.createCell(4).setCellValue(dto.getHorasTrabajadas());
            fila.createCell(5).setCellValue(dto.getDiasTrabajados());

            // Aplicar estilo a todas las celdas de la fila
            for (int i = 0; i < 6; i++) {
                fila.getCell(i).setCellStyle(estiloDatos);
            }
        }

        // Auto-ajustar anchos de columna
        for (int i = 0; i < encabezados.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Convertir el workbook a array de bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();

        return outputStream.toByteArray();
    }

    /**
     * Convierte el número de mes (1-12) a su nombre en español.
     */
    private String getNombreMes(int mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return meses[mes - 1];
    }
}