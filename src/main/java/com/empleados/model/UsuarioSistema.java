package com.empleados.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "UsuariosSistema")
public class UsuarioSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdUsuario")
    private Integer idUsuario;

    @Column(name = "Cedula")
    private String cedula;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Correo")
    private String correo;

    @Column(name = "Contrasenia")
    private String contrasenia;

    @Column(name = "Rol")
    private String rol;

    @Column(name = "Activo")
    private Boolean activo;

}