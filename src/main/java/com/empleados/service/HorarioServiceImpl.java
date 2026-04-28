package com.empleados.service;

import com.empleados.model.Horario;
import com.empleados.repository.HorarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion del servicio de gestion de horarios
 * 
 * Responsabilidades principales:
 * - Validar reglas de negocio antes de registrar entrada/salida
 * - Coordinar consultas al repositorio con filtros opcionales
 * - Gestionar el estado "En línea" / "Fuera de línea"
 * 
 * Validaciones implementadas:
 * 
 * 1. Entrada duplicada:
 *    - Busca si existe entrada activa (sin salida) para el usuario
 *    - Si existe, lanza excepción
 *    - Esto evita múltiples entradas abiertas simultáneamente
 * 
 * 2. Salida sin entrada:
 *    - Busca si existe entrada activa
 *    - Si NO existe, lanza excepción
 *    - El usuario debe marcar entrada antes de poder marcar salida
 * 
 * 3. Salida anterior a entrada:
 *    - Compara LocalDateTime.now() con la hora de entrada
 *    - Si la hora actual es anterior, lanza excepción
 *    - Protege contra inconsistencias del reloj del servidor
 * 
 * 4. Duplicación de salidas:
 *    - Implícitamente cubierta porque findEntradaActiva solo devuelve
 *      registros con HoraSalida IS NULL
 *    - Si ya tiene salida, no la encuentra y falla la validación #2
 * 
 * @Service: Componente de servicio registrado automaticamente por Spring.
 * @Autowired: Inyeccion del repositorio para acceso a datos.
 */


@Service
public class HorarioServiceImpl implements HorarioService {

    @Autowired
    private HorarioRepository repo;

    @Override
    public void registrarEntrada(Integer idUsuario) {

        // no pueden existir múltiples entradas sin salida previa
        Optional<Horario> entradaActiva = repo.findEntradaActiva(idUsuario);
        if (entradaActiva.isPresent()) {
            throw new IllegalStateException(
                    "Ya tiene una entrada activa. Registre la salida antes de marcar una nueva entrada.");
        }

        repo.registrarEntrada(idUsuario);
    }

    @Override
    public void registrarSalida(Integer idUsuario) {

        // no registrar salida sin entrada activa
        Optional<Horario> entradaActiva = repo.findEntradaActiva(idUsuario);
        if (entradaActiva.isEmpty()) {
            throw new IllegalStateException(
                    "No tiene una entrada activa. Registre la entrada antes de marcar la salida.");
        }

        Horario horario = entradaActiva.get();

        // salida no puede ser anterior a la entrada
        if (LocalDateTime.now().isBefore(horario.getHoraEntrada())) {
            throw new IllegalStateException(
                    "La hora de salida no puede ser anterior a la hora de entrada.");
        }

        repo.registrarSalida(horario.getIdHorario());
    }

    @Override
    public List<Horario> listarPorUsuario(Integer idUsuario, LocalDate fecha) {
        LocalDateTime inicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;
        return repo.findByUsuarioConFiltro(idUsuario, inicio, fin);
    }

    @Override
    public List<Horario> listarTodosConFiltros(LocalDate fecha, Integer idUsuario, String estado) {
        LocalDateTime inicio = (fecha != null) ? fecha.atStartOfDay() : null;
        LocalDateTime fin = (fecha != null) ? fecha.atTime(LocalTime.MAX) : null;
        String estadoParam = (estado != null && !estado.isBlank()) ? estado : null;
        return repo.findAllConFiltros(idUsuario, inicio, fin, estadoParam);
    }

    @Override
    public boolean tieneEntradaActiva(Integer idUsuario) {
        return repo.findEntradaActiva(idUsuario).isPresent();
    }
}
