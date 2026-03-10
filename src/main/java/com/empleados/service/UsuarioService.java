package com.empleados.service;

import com.empleados.model.UsuarioSistema;
import java.util.List;

public interface UsuarioService {

    List<UsuarioSistema> listar();

    void guardar(UsuarioSistema usuario);

    void modificar(UsuarioSistema usuario);

    void eliminar(Integer id);

}