package com.skillmaster.carrera;

import com.skillmaster.carrera.dto.CarreraResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** El usuario siempre se toma del token (claim "sub"), nunca del cuerpo ni de la URL. */
@RestController
@RequestMapping("/api/carreras")
@RequiredArgsConstructor
public class CarreraController {

    private final CarreraService carreraService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarreraResponse iniciar(@AuthenticationPrincipal Jwt jwt) {
        return carreraService.iniciar(usuarioId(jwt));
    }

    @PatchMapping("/{id}/finalizar")
    public CarreraResponse finalizar(@AuthenticationPrincipal Jwt jwt,
                                     @PathVariable Long id,
                                     @RequestParam double distanciaMetros) {
        return carreraService.finalizar(usuarioId(jwt), id, distanciaMetros);
    }

    @GetMapping
    public List<CarreraResponse> misCarreras(@AuthenticationPrincipal Jwt jwt) {
        return carreraService.listarPorUsuario(usuarioId(jwt));
    }

    private static Long usuarioId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
