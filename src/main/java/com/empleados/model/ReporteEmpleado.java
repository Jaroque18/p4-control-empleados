package com.empleados.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO (Data Transfer Object) para el reporte de horas trabajadas por empleado.
 * 
 * Este objeto encapsula la información agregada de un empleado durante un período específico:
 * - Datos del empleado (ID, nombre, cédula, correo)
 * - Total de horas trabajadas en el período
 * - Número de días trabajados
 * 
 * Se utiliza en:
 * - Generación de reportes en formato HTML (vista)
 * - Exportación a Excel
 * - Transmisión de datos entre capas (Service a Controller a Vista)
 * 
 * @Data: Genera automáticamente getters, setters, toString, equals y hashCode (Lombok).
 * @AllArgsConstructor: Genera constructor con todos los parámetros.
 * @NoArgsConstructor: Genera constructor sin parámetros par JPA.
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteEmpleado {

    private Integer idUsuario;
    private String cedula;
    private String nombre;
    private String correo;
    
    /**
     * Total de horas trabajadas en el periodo consultado.
     * Se calcula sumando la diferencia entre HoraSalida y HoraEntrada
     * de todos los registros completos del empleado en el rango de fechas.
     */
    private Double horasTrabajadas;
    
    /**
     * Numero de dias en que el empleado registro al menos una entrada
     * durante el periodo consultado.
     */
    private Integer diasTrabajados;
}