package com.empleados.service;

import com.empleados.model.UsuarioSistema;
import java.util.List;


 /**
 * Interfaz del servicio de gestión de usuarios.
 * 
 * Define el contrato o metodos publicos que debe cumplir cualquier implementacion.
 * 
 * Responsabilidades:
 * - Listar usuarios activos
 * - Crear nuevos usuarios (con hash de contraseña)
 * - Modificar usuarios existentes
 * - Inactivar usuarios (soft delete)
 * 
 * Esta capa contiene la logica de negocio:
 * - Validaciones de reglas de negocio
 * - Transformacion de datos
 * - Coordinacion entre multiples repositorios si fuera necesario
 */

public interface UsuarioService {

    List<UsuarioSistema> listar();

    void guardar(UsuarioSistema usuario);

    void modificar(UsuarioSistema usuario);

    void eliminar(Integer id);

}