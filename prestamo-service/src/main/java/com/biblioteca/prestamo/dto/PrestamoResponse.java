package com.biblioteca.prestamo.dto;

import com.biblioteca.prestamo.model.EstadoPrestamo;
import com.biblioteca.prestamo.model.Prestamo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PrestamoResponse(
        Long id,
        Long lectorId,
        Long libroId,
        LocalDate fechaPrestamo,
        LocalDate fechaLimite,
        LocalDate fechaDevolucion,
        EstadoPrestamo estado,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {

    public static PrestamoResponse from(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getLectorId(),
                prestamo.getLibroId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaLimite(),
                prestamo.getFechaDevolucion(),
                prestamo.getEstado(),
                prestamo.getCreadoEn(),
                prestamo.getActualizadoEn()
        );
    }
}
