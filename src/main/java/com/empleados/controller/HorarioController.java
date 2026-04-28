package com.empleados.controller;

import com.empleados.model.Horario;
import com.empleados.model.UsuarioSistema;
import com.empleados.repository.UsuarioRepository;
import com.empleados.service.HorarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HorarioController {

    @Autowired
    private HorarioService horarioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/users/horarios")
    public String misHorarios(
        //año-mes-día ISO a LOCAL no es requerido ya que es para filtar
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            Model model, Authentication auth) {

        UsuarioSistema usuario = obtenerUsuarioAutenticado(auth);

        List<Horario> horarios = horarioService.listarPorUsuario(usuario.getIdUsuario(), fecha);
        boolean tieneEntradaActiva = horarioService.tieneEntradaActiva(usuario.getIdUsuario());

        model.addAttribute("horarios", horarios);
        model.addAttribute("tieneEntradaActiva", tieneEntradaActiva);
        model.addAttribute("usuario", usuario);
        model.addAttribute("fechaFiltro", fecha);
        return "users/horarios";
    }

    @PostMapping("/users/marcarEntrada")
    public String marcarEntrada(Authentication auth, RedirectAttributes flash) {
        try {
            UsuarioSistema usuario = obtenerUsuarioAutenticado(auth);
            horarioService.registrarEntrada(usuario.getIdUsuario());
            flash.addFlashAttribute("success", "Entrada registrada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users/horarios";
    }

    @PostMapping("/users/marcarSalida")
    public String marcarSalida(Authentication auth, RedirectAttributes flash) {
        try {
            UsuarioSistema usuario = obtenerUsuarioAutenticado(auth);
            horarioService.registrarSalida(usuario.getIdUsuario());
            flash.addFlashAttribute("success", "Salida registrada correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/users/horarios";
    }

    @GetMapping("/admin/horarios")
    public String todosLosHorarios(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer idUsuario,
            @RequestParam(required = false) String estado,
            Model model) {

        List<Horario> horarios = horarioService.listarTodosConFiltros(fecha, idUsuario, estado);
        List<UsuarioSistema> usuarios = usuarioRepository.listarUsuarios();

        // se mapea si esta activo o inactivo, para mostrar el boton de salir
        Map<Integer, Boolean> estadoEmpleados = new HashMap<>();
        for (UsuarioSistema u : usuarios) {
            estadoEmpleados.put(u.getIdUsuario(), horarioService.tieneEntradaActiva(u.getIdUsuario()));
        }

        model.addAttribute("horarios", horarios);
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("estadoEmpleados", estadoEmpleados);
        model.addAttribute("fechaFiltro", fecha);
        model.addAttribute("idUsuarioFiltro", idUsuario);
        model.addAttribute("estadoFiltro", estado);
        return "admin/dashboardHorarios";
    }

    @PostMapping("/admin/horarios/marcarEntrada/{idUsuario}")
    public String marcarEntradaAdmin(@PathVariable Integer idUsuario, RedirectAttributes flash) {
        try {
            horarioService.registrarEntrada(idUsuario);
            flash.addFlashAttribute("success", "Entrada registrada para el empleado.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/horarios";
    }

    @PostMapping("/admin/horarios/marcarSalida/{idUsuario}")
    public String marcarSalidaAdmin(@PathVariable Integer idUsuario, RedirectAttributes flash) {
        try {
            horarioService.registrarSalida(idUsuario);
            flash.addFlashAttribute("success", "Salida registrada para el empleado.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/horarios";
    }

    private UsuarioSistema obtenerUsuarioAutenticado(Authentication auth) {
        return usuarioRepository.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException(
                        "Usuario autenticado no encontrado en la base de datos."));
    }
}
