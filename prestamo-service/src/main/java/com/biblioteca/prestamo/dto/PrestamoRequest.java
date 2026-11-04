package com.biblioteca.prestamo.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PrestamoRequest(
        @NotNull(message = "El ID del lector es obligatorio")
        Long lectorId,

        @NotNull(message = "El ID del libro es obligatorio")
        Long libroId,

        @NotNull(message = "La fecha de préstamo es obligatoria")
        LocalDate fechaPrestamo,

        @NotNull(message = "La fecha límite es obligatoria")
        LocalDate fechaLimite
) {

    @AssertTrue(message = "La fecha límite no puede ser anterior a la fecha de préstamo")
    public boolean isFechaLimiteValida() {
        return fechaPrestamo == null || fechaLimite == null || !fechaLimite.isBefore(fechaPrestamo);
    }
}
