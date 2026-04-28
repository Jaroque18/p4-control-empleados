package com.empleados.repository;

import com.empleados.model.UsuarioSistema;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para acceso a datos de usuarios.
 * 
 * Esta interfaz extiende JpaRepository, lo que proporciona automaticamente:
 * - save(): Insertar o actualizar
 * - findById(): Buscar por ID
 * - findAll(): Listar todos
 * - deleteById(): Eliminar por ID
 * - count(): Contar registros
 * - Y muchos metodos sin necesidad de implementarlos
 * 
 * Adicionalmente, define queries personalizadas usando:
 * - @Query con SQL nativo (nativeQuery = true)
 * - Invocación de Stored Procedures
 * 
 * Stored Procedures en lugar de JPA nativo
 * - Centraliza la lógica de negocio de BD en un solo lugar
 * - Permite reutilización desde multiples aplicaciones
 * - Facilita cambios sin modificar codigo Java
 * - Mejor performance en operaciones complejas ya que el motor de BD las optimiza
 * 
 * @Query: Define consultas personalizadas.
 *         - nativeQuery = true: SQL directo (no JPQL)
 *         - @Param: Mapea parámetros del método a placeholders en la query (:nombre)
 * 
 * @Modifying: Marca queries que modifican datos (INSERT, UPDATE, DELETE).
 *             Sin esta anotación, Spring asume que es una consulta SELECT.
 * 
 * @Transactional: Asegura que la operación se ejecute dentro de una transaccion.
 *                 Si falla, se hace rollback automático.
 * 
 * Spring detecta automaticamente las interfaces que extienden JpaRepository
 * y genera la implementacion en tiempo de ejecución como Proxy.
 */


public interface UsuarioRepository extends JpaRepository<UsuarioSistema, Integer> {

        @Query(value = "SELECT * FROM UsuariosSistema WHERE Correo = :correo AND Activo = 1", nativeQuery = true)
        Optional<UsuarioSistema> findByCorreo(@Param("correo") String correo);

        @Query(value = "EXEC usp_UsuarioSistema_Accion 'LISTAR'", nativeQuery = true)
        List<UsuarioSistema> listarUsuarios();

        @Modifying
        @Transactional
        @Query(value = "EXEC usp_UsuarioSistema_Accion 'INSERTAR', NULL, :cedula, :nombre, :correo, :contrasenia, :rol", nativeQuery = true)
        void insertarUsuarios(
                        @Param("cedula") String cedula,
                        @Param("nombre") String nombre,
                        @Param("correo") String correo,
                        @Param("contrasenia") String contrasenia,
                        @Param("rol") String rol);

        @Modifying
        @Transactional
        @Query(value = "EXEC usp_UsuarioSistema_Accion 'MODIFICAR', :id, :cedula, :nombre, :correo, NULL, :rol", nativeQuery = true)
        void modificarUsuarios(
                        @Param("id") Integer id,
                        @Param("cedula") String cedula,
                        @Param("nombre") String nombre,
                        @Param("correo") String correo,
                        @Param("rol") String rol);

        @Modifying
        @Transactional
        @Query(value = "EXEC usp_UsuarioSistema_Accion 'INACTIVAR', :idUsuario", nativeQuery = true)
        void inactivarUsuarios(
                        @Param("idUsuario") Integer idUsuario);

}