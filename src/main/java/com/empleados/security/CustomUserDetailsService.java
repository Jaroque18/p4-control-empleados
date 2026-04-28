package com.empleados.security;

import com.empleados.model.UsuarioSistema;
import com.empleados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio personalizado de carga de usuarios para Spring Security.
 * 
 * Esta clase es el puente entre Spring Security y la BD de usuarios.
 * Spring Security la invoca automaticamente durante el proceso de login.
 * 
 * Flujo de autenticacion:
 * 1. Usuario envía credenciales (correo + contraseña) al formulario de login
 * 2. Spring Security intercepta la petición POST a /login
 * 3. Spring Security llama a loadUserByUsername(correo)
 * 4. Este metodo busca el usuario en BD por correo
 * 5. Devuelve un objeto User de Spring Security con:
 *    - Username (correo)
 *    - Password (hash BCrypt de BD)
 *    - Authorities (roles, ej: ROLE_ADMIN)
 * 6. Spring Security compara la contraseña ingresada con el hash usando BCrypt
 * 7. Si coincide, crea la sesion y redirige según el rol
 * 8. Si no coincide, redirige a /login?error
 * 
 * @Service: Registra esta clase como bean de Spring.
 *           Spring Security la detecta automaticamente porque implementa UserDetailsService.
 * 
 * UserDetailsService:
 * Interfaz de Spring Security con un solo metodo: loadUserByUsername()
 * 
 * SimpleGrantedAuthority:
 * Representa un rol o permiso en Spring Security.
 * IMPORTANTE: Los roles en Spring Security deben tener prefijo "ROLE_"
 * - En BD guardamos: "ADMIN"
 * - En Spring Security: "ROLE_ADMIN"
 * - En @PreAuthorize o hasRole(): solo "ADMIN"
 * 
 * UsernameNotFoundException?
 * Es la excepcion para indicar que el usuario no existe.
 * Spring Security la captura y maneja apropiadamente redirige a login.
 */


@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //buscar el usuario por correo
        UsuarioSistema usuario = repo.findByCorreo(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new User(
                usuario.getCorreo(),
                usuario.getContrasenia(),
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())));
    }
}