package com.empleados.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "Horarios")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdHorario")
    private Integer idHorario;

    @Column(name = "IdUsuario")
    private Integer idUsuario;

    @Column(name = "HoraEntrada")
    private LocalDateTime horaEntrada;

    @Column(name = "HoraSalida")
    private LocalDateTime horaSalida;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdUsuario", insertable = false, updatable = false)
    private UsuarioSistema usuario;

    @Transient
    public String getEstado() {
        if (horaEntrada != null && horaSalida == null) {
            return "En línea";
        }
        return "Fuera de línea";
    }
}
