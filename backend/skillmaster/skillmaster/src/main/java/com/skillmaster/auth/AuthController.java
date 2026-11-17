package com.skillmaster.auth;

import com.skillmaster.auth.dto.LoginRequest;
import com.skillmaster.auth.dto.RegistroRequest;
import com.skillmaster.auth.dto.TokenResponse;
import com.skillmaster.auth.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Crear usuario. Devuelve el token para que la app quede logueada directamente. */
    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse registro(@Valid @RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    /** Ingresar con email y contraseña. */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    /** Datos del usuario dueño del token. */
    @GetMapping("/me")
    public UsuarioResponse me(@AuthenticationPrincipal Jwt jwt) {
        return authService.perfil(Long.valueOf(jwt.getSubject()));
    }
}
