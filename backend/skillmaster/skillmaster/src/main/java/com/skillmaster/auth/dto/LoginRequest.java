package com.skillmaster.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Credenciales para ingresar. */
public record LoginRequest(
        @NotBlank String email,
        @NotBlank String password
) {
}
