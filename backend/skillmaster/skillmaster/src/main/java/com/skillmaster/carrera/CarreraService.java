package com.skillmaster.carrera;

import com.skillmaster.carrera.dto.CarreraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CarreraService {

    /** Velocidad máxima razonable corriendo (~25 km/h). Por encima se considera trampa. */
    private static final double VELOCIDAD_MAXIMA_MS = 7.0;

    private final CarreraRepository carreraRepository;

    @Transactional
    public CarreraResponse iniciar(Long usuarioId) {
        Carrera carrera = Carrera.builder()
                .usuarioId(usuarioId)
                .fechaInicio(LocalDateTime.now())
                .estado(Carrera.EstadoCarrera.EN_CURSO)
                .build();
        return CarreraResponse.from(carreraRepository.save(carrera));
    }

    @Transactional
    public CarreraResponse finalizar(Long usuarioId, Long id, double distanciaMetros) {
        Carrera carrera = carreraRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Carrera no encontrada: " + id));

        if (!carrera.getUsuarioId().equals(usuarioId)) {
            throw new AccessDeniedException("La carrera no pertenece al usuario");
        }

        if (carrera.getEstado() != Carrera.EstadoCarrera.EN_CURSO) {
            throw new IllegalStateException("La carrera ya no está en curso");
        }

        LocalDateTime fin = LocalDateTime.now();
        long segundos = Duration.between(carrera.getFechaInicio(), fin).getSeconds();

        carrera.setFechaFin(fin);
        carrera.setDistanciaMetros(distanciaMetros);
        carrera.setDuracionSegundos(segundos);
        carrera.setEstado(esSospechosa(distanciaMetros, segundos)
                ? Carrera.EstadoCarrera.INVALIDADA
                : Carrera.EstadoCarrera.FINALIZADA);

        return CarreraResponse.from(carrera);
    }

    public List<CarreraResponse> listarPorUsuario(Long usuarioId) {
        return carreraRepository.findByUsuarioIdOrderByFechaInicioDesc(usuarioId).stream()
                .map(CarreraResponse::from)
                .toList();
    }

    /** Regla anti-trampas básica: velocidad media imposible para una persona corriendo. */
    private boolean esSospechosa(double distanciaMetros, long segundos) {
        if (segundos <= 0) {
            return distanciaMetros > 0;
        }
        return (distanciaMetros / segundos) > VELOCIDAD_MAXIMA_MS;
    }
}
