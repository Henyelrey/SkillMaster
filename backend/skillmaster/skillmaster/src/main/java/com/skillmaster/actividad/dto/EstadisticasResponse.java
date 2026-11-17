package com.skillmaster.actividad.dto;

import com.skillmaster.actividad.ResumenSemanal;

import java.util.List;

/** Datos que consume la pantalla de perfil. */
public record EstadisticasResponse(
        Long usuarioId,
        int totalCarreras,
        double distanciaTotalMetros,
        long duracionTotalSegundos,
        List<ResumenSemanal> semanas
) {
}
