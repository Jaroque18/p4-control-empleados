package com.empleados.service;

import com.empleados.model.UsuarioSistema;
import com.empleados.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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