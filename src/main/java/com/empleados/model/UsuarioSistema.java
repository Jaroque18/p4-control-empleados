package com.empleados.model;
import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA que representa un usuario del sistema.
 * 
 * Esta clase mapea directamente a la tabla UsuariosSistema en la base de datos.
 * Cada instancia de esta clase representa una fila de la tabla.
 * 
 * Campos:
 * - idUsuario: Clave primaria auto-generada
 * - cedula: Identificador unico del usuario (no modificable despues de creacion)
 * - nombre: Nombre completo del usuario
 * - correo: Email usado como username en Spring Security
 * - contrasenia: Hash BCrypt de la contraseña NUNCA se almacena en texto plano
 * - rol: ADMIN o EMPLEADO
 * - activo: Soft delete 1 = activo, 0 = inactivo
 * 
 * Anotaciones de JPA:
 * 
 * @Entity: Marca esta clase como entidad JPA. Hibernate la detecta automaticamente
 *          y crea las queries SQL necesarias para operaciones CRUD.
 * 
 * @Table: Especifica el nombre exacto de la tabla en la BD.
 *         Si no se usa esta anotacion, JPA usa el nombre de la clase.
 * 
 * @Id: Marca el campo como clave primaria.
 * 
 * @GeneratedValue(strategy = IDENTITY): La BD genera automáticamente el valor
 *                                        usando autoincremento (IDENTITY en SQL Server).
 * 
 * @Column: Mapea el campo Java al nombre exacto de la columna en la BD.
 *          Necesario cuando los nombres no coinciden exactamente.
 * 
 * Anotaciones de Lombok:
 * 
 * @Data: Genera automaticamente en tiempo de compilacion:
 *        - Getters para todos los campos
 *        - Setters para todos los campos
 *        - toString()
 *        - equals() y hashCode()
 */

@Data
@Entity
@Table(name = "UsuariosSistema")
public class UsuarioSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario")
    private Integer idUsuario;

    @Column(name = "Cedula")
    private String cedula;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Correo")
    private String correo;

    @Column(name = "Contrasenia")
    private String contrasenia;

    @Column(name = "Rol")
    private String rol;

    @Column(name = "Activo")
    private Boolean activo;

}