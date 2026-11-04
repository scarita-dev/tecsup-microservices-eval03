package com.biblioteca.prestamo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LibroResumen(
        Long id,
        String isbn,
        String titulo,
        String autor,
        Integer stock,
        String estado
) {
}
