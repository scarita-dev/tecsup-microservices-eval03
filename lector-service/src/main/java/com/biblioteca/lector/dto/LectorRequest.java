package com.biblioteca.lector.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LectorRequest(
        @NotBlank(message = "El documento es obligatorio")
        @Pattern(regexp = "^[A-Za-z0-9-]{6,20}$", message = "El documento debe tener entre 6 y 20 caracteres alfanuméricos o guiones")
        String documento,

        @NotBlank(message = "Los nombres son obligatorios")
        @Size(max = 150, message = "Los nombres no deben superar 150 caracteres")
        String nombres,

        @NotBlank(message = "El contacto es obligatorio")
        @Size(max = 120, message = "El contacto no debe superar 120 caracteres")
        String contacto
) {
}
