package com.skillmaster.auth.dto;

/**
 * Respuesta con formato OAuth2 (RFC 6749 §5.1): la app guarda el access_token
 * y lo envía en cada petición como "Authorization: Bearer &lt;token&gt;".
 */
public record TokenResponse(
        String access_token,
        String token_type,
        long expires_in,
        UsuarioResponse usuario
) {
    public static TokenResponse bearer(String token, long expiresIn, UsuarioResponse usuario) {
        return new TokenResponse(token, "Bearer", expiresIn, usuario);
    }
}
