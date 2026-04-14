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
