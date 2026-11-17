package com.skillmaster.carrera;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    List<Carrera> findByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);

    List<Carrera> findByUsuarioIdAndEstadoAndFechaInicioBetween(
            Long usuarioId, Carrera.EstadoCarrera estado, LocalDateTime desde, LocalDateTime hasta);
}
