package com.empleados.repository;

import com.empleados.model.Horario;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {

       @Query(value = "SELECT TOP 1 * FROM Horarios " +
                     "WHERE IdUsuario = :idUsuario AND HoraSalida IS NULL " +
                     "ORDER BY HoraEntrada DESC", nativeQuery = true)
       Optional<Horario> findEntradaActiva(@Param("idUsuario") Integer idUsuario);

       @Query("SELECT h FROM Horario h " +
                     "WHERE h.idUsuario = :idUsuario " +
                     "AND (:inicio IS NULL OR h.horaEntrada >= :inicio) " +
                     "AND (:fin    IS NULL OR h.horaEntrada <= :fin) " +
                     "ORDER BY h.horaEntrada DESC")
       List<Horario> findByUsuarioConFiltro(
                     @Param("idUsuario") Integer idUsuario,
                     @Param("inicio") LocalDateTime inicio,
                     @Param("fin") LocalDateTime fin);

       @Query("SELECT h FROM Horario h JOIN FETCH h.usuario u " +
                     "WHERE (:idUsuario IS NULL OR h.idUsuario = :idUsuario) " +
                     "AND   (:inicio    IS NULL OR h.horaEntrada >= :inicio) " +
                     "AND   (:fin       IS NULL OR h.horaEntrada <= :fin) " +
                     "AND   (" +
                     "       :estado IS NULL " +
                     "    OR (:estado = 'online'  AND h.horaSalida IS NULL) " +
                     "    OR (:estado = 'offline' AND h.horaSalida IS NOT NULL)" +
                     ") " +
                     "ORDER BY h.horaEntrada DESC")
       List<Horario> findAllConFiltros(
                     @Param("idUsuario") Integer idUsuario,
                     @Param("inicio") LocalDateTime inicio,
                     @Param("fin") LocalDateTime fin,
                     @Param("estado") String estado);

       @Modifying
       @Transactional
       @Query(value = "EXEC usp_Horario_Accion 'REGISTRAR_ENTRADA', :idUsuario, NULL", nativeQuery = true)
       void registrarEntrada(@Param("idUsuario") Integer idUsuario);

       @Modifying
       @Transactional
       @Query(value = "EXEC usp_Horario_Accion 'REGISTRAR_SALIDA', NULL, :idHorario", nativeQuery = true)
       void registrarSalida(@Param("idHorario") Integer idHorario);
}
