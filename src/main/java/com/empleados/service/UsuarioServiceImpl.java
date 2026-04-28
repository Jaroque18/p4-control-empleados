package com.empleados.service;

import com.empleados.model.UsuarioSistema;
import com.empleados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Implementacion del servicio de gestión de usuarios.
 * 
 * Esta clase contiene toda la logica de negocio relacionada con usuarios:
 * - Encriptación de contraseñas antes de guardar
 * - Orquestación de llamadas a Stored Procedures
 * - Manejo de excepciones de BD
 * 
 * @Service: Marca esta clase como componente de servicio de Spring.
 *           Spring la detecta automaticamente y la registra en el contenedor de Control.
 *           Se crea como singleton una sola instancia para toda la aplicacion.
 * 
 * @Autowired: Inyeccion de dependencias.
 *             Spring busca beans que coincidan con el tipo e inyecta automáticamente.
 *             
 *             Aqui se inyectan:
 *             - UsuarioRepository: Para acceso a datos
 *             - PasswordEncoder: Para hashear contraseñas
 *             
 *             Ventajas vs. new:
 *             - No necesitamos gestionar el ciclo de vida de las dependencias
 *             - Spring gestiona todo automaticamente
 *             - Facilita el testing (se pueden inyectar mocks)
 * 
 * Patron de capas:
 * Controller a Service a Repository a BD
 * 
 * Se implementa hasheo de contraseñas para
 * - Nunca almacenar contraseñas en texto plano
 * - Si la BD se compromete, las contraseñas reales están protegidas
 * - BCrypt es unidireccional.
 * - Cada hash incluye un salt aleatorio, dos contraseñas iguales producen hashes diferentes
 */


@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioRepository repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioSistema> listar() {
        return repo.listarUsuarios();
    }

    @Override
    public void guardar(UsuarioSistema usuario) {

        usuario.setContrasenia(
                passwordEncoder.encode(usuario.getContrasenia()));

        repo.insertarUsuarios(
                usuario.getCedula(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getContrasenia(),
                usuario.getRol());

    }

    @Override
    public void modificar(UsuarioSistema usuario) {

        repo.modificarUsuarios(
                usuario.getIdUsuario(),
                usuario.getCedula(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol());

    }

    @Override
    public void eliminar(Integer id) {
        repo.inactivarUsuarios(id);
    }

}