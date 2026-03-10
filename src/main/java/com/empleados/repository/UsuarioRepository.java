package com.empleados.repository;

import com.empleados.model.UsuarioSistema;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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