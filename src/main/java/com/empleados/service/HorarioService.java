package com.empleados.service;

import com.empleados.model.Horario;
import java.time.LocalDate;
import java.util.List;

public interface HorarioService {

    void registrarEntrada(Integer idUsuario);

    void registrarSalida(Integer idUsuario);

    List<Horario> listarPorUsuario(Integer idUsuario, LocalDate fecha);

    List<Horario> listarTodosConFiltros(LocalDate fecha, Integer idUsuario, String estado);

    boolean tieneEntradaActiva(Integer idUsuario);
}
