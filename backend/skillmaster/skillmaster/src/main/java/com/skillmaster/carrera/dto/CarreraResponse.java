package com.skillmaster.carrera.dto;

import com.skillmaster.carrera.Carrera;

import java.time.LocalDateTime;

/** Datos que se devuelven a la app móvil. */
public record CarreraResponse(
        Long id,
        Long usuarioId,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        Double distanciaMetros,
        Long duracionSegundos,
        String estado
) {
    public static CarreraResponse from(Carrera c) {
        return new CarreraResponse(
                c.getId(),
                c.getUsuarioId(),
                c.getFechaInicio(),
                c.getFechaFin(),
                c.getDistanciaMetros(),
                c.getDuracionSegundos(),
                c.getEstado().name()
        );
    }
}
