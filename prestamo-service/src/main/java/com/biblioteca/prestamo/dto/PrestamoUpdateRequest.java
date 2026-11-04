package com.biblioteca.prestamo.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PrestamoUpdateRequest(
        @NotNull(message = "La fecha límite es obligatoria")
        LocalDate fechaLimite
) {
}
