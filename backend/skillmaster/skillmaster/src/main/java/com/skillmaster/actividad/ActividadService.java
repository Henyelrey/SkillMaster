package com.skillmaster.actividad;

import com.skillmaster.actividad.dto.EstadisticasResponse;
import com.skillmaster.carrera.Carrera;
import com.skillmaster.carrera.CarreraRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final CarreraRepository carreraRepository;

    public EstadisticasResponse obtenerEstadisticas(Long usuarioId, int semanas) {
        if (semanas < 1) {
            throw new IllegalArgumentException("El número de semanas debe ser al menos 1");
        }

        LocalDate lunesActual = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime desde = lunesActual.minusWeeks(semanas - 1L).atStartOfDay();
        LocalDateTime hasta = LocalDateTime.now();

        List<Carrera> carreras = carreraRepository.findByUsuarioIdAndEstadoAndFechaInicioBetween(
                usuarioId, Carrera.EstadoCarrera.FINALIZADA, desde, hasta);

        Map<LocalDate, List<Carrera>> porSemana = carreras.stream()
                .collect(Collectors.groupingBy(c -> c.getFechaInicio().toLocalDate()
                        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))));

        List<ResumenSemanal> resumenes = new ArrayList<>();
        for (int i = 0; i < semanas; i++) {
            LocalDate inicio = lunesActual.minusWeeks(i);
            resumenes.add(resumir(inicio, porSemana.getOrDefault(inicio, List.of())));
        }
        resumenes.sort(Comparator.comparing(ResumenSemanal::inicioSemana));

        ResumenSemanal total = resumir(null, carreras);
        return new EstadisticasResponse(
                usuarioId,
                total.totalCarreras(),
                total.distanciaTotalMetros(),
                total.duracionTotalSegundos(),
                resumenes
        );
    }

    private ResumenSemanal resumir(LocalDate inicio, List<Carrera> carreras) {
        return new ResumenSemanal(
                inicio,
                carreras.size(),
                carreras.stream().mapToDouble(c -> c.getDistanciaMetros() == null ? 0 : c.getDistanciaMetros()).sum(),
                carreras.stream().mapToLong(c -> c.getDuracionSegundos() == null ? 0 : c.getDuracionSegundos()).sum()
        );
    }
}
