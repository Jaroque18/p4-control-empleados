package com.empleados.controller;

import com.empleados.model.UsuarioSistema;
import com.empleados.service.UsuarioService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping("/users/inicio")
    public String inicio() {
        return "users/inicio"; 
    }

    @GetMapping("/users/accesoDenegado")
    public String accesoDenegado() {
        return "error/403"; 
    }

    @GetMapping("/admin/usuarios")
    public String listar(Model model) {
        List<UsuarioSistema> usuarios = service.listar();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("usuario", new UsuarioSistema());
        return "admin/dashboardUsuarios";
    }

    @PostMapping("/admin/guardar")
    public String guardar(@ModelAttribute UsuarioSistema usuario, RedirectAttributes flash) {
        try {
            if (usuario.getIdUsuario() == null) {
                service.guardar(usuario);
                flash.addFlashAttribute("success", "Usuario creado correctamente");
            } else {
                service.modificar(usuario);
                flash.addFlashAttribute("success", "Usuario actualizado correctamente");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/admin/eliminar/{id}")
    public String eliminar(@PathVariable Integer id, RedirectAttributes flash) {
        try {
            service.eliminar(id);
            flash.addFlashAttribute("success", "Usuario eliminado correctamente");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo eliminar: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}