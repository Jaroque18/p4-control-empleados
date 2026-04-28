package com.empleados.service;

import com.empleados.model.Horario;
import java.time.LocalDate;
import java.util.List;

/**
 * Interfaz del servicio de gestión de horarios.
 * 
 * Define el contrato para:
 * - Registro de entrada y salida
 * - Consulta de historial con filtros
 * - Validación de reglas de negocio de horarios
 * 
 * Reglas de negocio que debe garantizar la implementacion:
 * - No pueden existir múltiples entradas sin salida
 * - No se puede registrar salida sin entrada activa
 * - La salida no puede ser anterior a la entrada
 * - No pueden existir duplicación de salidas
 * 
 * Estas validaciones se implementan en HorarioServiceImpl.
 */


public interface HorarioService {

    void registrarEntrada(Integer idUsuario);

    void registrarSalida(Integer idUsuario);

    List<Horario> listarPorUsuario(Integer idUsuario, LocalDate fecha);

    List<Horario> listarTodosConFiltros(LocalDate fecha, Integer idUsuario, String estado);

    boolean tieneEntradaActiva(Integer idUsuario);
}
