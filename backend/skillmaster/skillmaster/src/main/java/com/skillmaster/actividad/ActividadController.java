package com.skillmaster.actividad;

import com.skillmaster.actividad.dto.EstadisticasResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actividad")
@RequiredArgsConstructor
public class ActividadController {

    private final ActividadService actividadService;

    /** Estadísticas del usuario autenticado (tomado del token). */
    @GetMapping("/estadisticas")
    public EstadisticasResponse estadisticas(@AuthenticationPrincipal Jwt jwt,
                                             @RequestParam(defaultValue = "4") int semanas) {
        return actividadService.obtenerEstadisticas(Long.valueOf(jwt.getSubject()), semanas);
    }
}
