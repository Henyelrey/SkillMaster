package com.skillmaster.auth;

import com.skillmaster.auth.dto.LoginRequest;
import com.skillmaster.auth.dto.RegistroRequest;
import com.skillmaster.auth.dto.TokenResponse;
import com.skillmaster.auth.dto.UsuarioResponse;
import com.skillmaster.usuario.Usuario;
import com.skillmaster.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Transactional
    public TokenResponse registrar(RegistroRequest request) {
        String email = normalizar(request.email());
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailYaRegistradoException(email);
        }
        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .email(email)
                .nombre(request.nombre().trim())
                .passwordHash(passwordEncoder.encode(request.password()))
                .rol(Usuario.Rol.USUARIO)
                .fechaRegistro(LocalDateTime.now())
                .build());
        return emitirToken(usuario);
    }

    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(normalizar(request.email()))
                .filter(u -> passwordEncoder.matches(request.password(), u.getPasswordHash()))
                // Mismo mensaje si no existe el email o la contraseña es incorrecta,
                // para no revelar qué emails están registrados.
                .orElseThrow(() -> new BadCredentialsException("Email o contraseña incorrectos"));
        return emitirToken(usuario);
    }

    public UsuarioResponse perfil(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(UsuarioResponse::from)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado: " + usuarioId));
    }

    private TokenResponse emitirToken(Usuario usuario) {
        return TokenResponse.bearer(
                tokenService.generar(usuario),
                tokenService.getExpiracionSegundos(),
                UsuarioResponse.from(usuario));
    }

    private static String normalizar(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public static class EmailYaRegistradoException extends RuntimeException {
        public EmailYaRegistradoException(String email) {
            super("Ya existe una cuenta con el email " + email);
        }
    }
}
