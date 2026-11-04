package com.biblioteca.prestamo.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LectorResumen(
        Long id,
        String documento,
        String nombres,
        String contacto
) {
}
