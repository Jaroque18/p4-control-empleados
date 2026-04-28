package com.empleados.model;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro de entrada/salida de un empleado.
 * 
 * Mapea a la tabla Horarios en la base de datos.
 * Cada registro guarda una entrada y opcionalmente su salida correspondiente.
 * 
 * Campos:
 * - idHorario: Clave primaria auto-generada
 * - idUsuario: FK a UsuariosSistema
 * - horaEntrada: Timestamp de cuando el empleado marco entrada (NOT NULL)
 * - horaSalida: Timestamp de cuando marcó salida (NULL si aún está en línea)
 * - usuario: Relacion JPA al objeto UsuarioSistema completo
 * 
 * Relaciones:
 * 
 * @ManyToOne: Muchos horarios pertenecen a un usuario.
 *             Esto NO crea una columna adicional usa la FK existente IdUsuario.
 * 
 * @JoinColumn con insertable=false, updatable=false:
 *             Indica que esta relacion es de solo lectura.
 *             La columna IdUsuario se gestiona mediante el campo idUsuario
 *             no mediante esta relacion.
 * 
 * FetchType.LAZY:
 *             El usuario asociado NO se carga automaticamente cuando se consulta un horario.
 *             Solo se carga si se accede explícitamente a h.getUsuario().
 *             
 *             Ventajas:
 *             - Evita consultas innecesarias a la BD
 *             - Mejora el rendimiento cuando solo se necesitan datos del horario
 * 
 * @Transient: Marca un metodo/campo como NO persistente.
 *             getEstado() es un calculo en memoria, no una columna de BD.
 *             Se calcula dinamicamente cada vez que se llama.
 */


@Data
@Entity
@Table(name = "Horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdHorario")
    private Integer idHorario;

    @Column(name = "IdUsuario")
    private Integer idUsuario;

    @Column(name = "HoraEntrada")
    private LocalDateTime horaEntrada;

    @Column(name = "HoraSalida")
    private LocalDateTime horaSalida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdUsuario", insertable = false, updatable = false)
    private UsuarioSistema usuario;

    @Transient
    public String getEstado() {
        if (horaEntrada != null && horaSalida == null) {
            return "En línea";
        }
        return "Fuera de línea";
    }
}
