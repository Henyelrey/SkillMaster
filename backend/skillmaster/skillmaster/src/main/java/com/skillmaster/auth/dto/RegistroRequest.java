package com.skillmaster.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos que envía la app móvil para crear una cuenta. */
public record RegistroRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(max = 60) String nombre,
        @NotBlank @Size(min = 8, max = 72) String password
) {
}
