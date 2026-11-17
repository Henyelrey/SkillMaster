package com.skillmaster.actividad;

import java.time.LocalDate;

/** Modelo simple (no es @Entity): resumen de actividad de una semana. */
public record ResumenSemanal(
        LocalDate inicioSemana,
        int totalCarreras,
        double distanciaTotalMetros,
        long duracionTotalSegundos
) {
}
