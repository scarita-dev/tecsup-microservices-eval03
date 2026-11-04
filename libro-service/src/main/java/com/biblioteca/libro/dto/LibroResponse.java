package com.biblioteca.libro.dto;

import com.biblioteca.libro.model.EstadoLibro;
import com.biblioteca.libro.model.Libro;

import java.time.LocalDateTime;

public record LibroResponse(
        Long id,
        String isbn,
        String titulo,
        String autor,
        Integer stock,
        EstadoLibro estado,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {

    public static LibroResponse from(Libro libro) {
        return new LibroResponse(
                libro.getId(),
                libro.getIsbn(),
                libro.getTitulo(),
                libro.getAutor(),
                libro.getStock(),
                libro.getEstado(),
                libro.getCreadoEn(),
                libro.getActualizadoEn()
        );
    }
}
