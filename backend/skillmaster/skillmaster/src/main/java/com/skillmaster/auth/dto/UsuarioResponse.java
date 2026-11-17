package com.skillmaster.auth.dto;

import com.skillmaster.usuario.Usuario;

/** Datos públicos del usuario (sin la contraseña). */
public record UsuarioResponse(
        Long id,
        String email,
        String nombre,
        String rol
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getEmail(), u.getNombre(), u.getRol().name());
    }
}
