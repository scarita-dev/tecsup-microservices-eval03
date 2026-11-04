package com.biblioteca.libro.dto;

import com.biblioteca.libro.model.EstadoLibro;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LibroRequest(
        @NotBlank(message = "El ISBN es obligatorio")
        @Size(max = 30, message = "El ISBN no debe superar 30 caracteres")
        String isbn,

        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no debe superar 150 caracteres")
        String titulo,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 120, message = "El autor no debe superar 120 caracteres")
        String autor,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @NotNull(message = "El estado es obligatorio")
        EstadoLibro estado
) {
}
