package com.biblioteca.lector.dto;

import com.biblioteca.lector.model.Lector;

import java.time.LocalDateTime;

public record LectorResponse(
        Long id,
        String documento,
        String nombres,
        String contacto,
        LocalDateTime creadoEn,
        LocalDateTime actualizadoEn
) {

    public static LectorResponse from(Lector lector) {
        return new LectorResponse(
                lector.getId(),
                lector.getDocumento(),
                lector.getNombres(),
                lector.getContacto(),
                lector.getCreadoEn(),
                lector.getActualizadoEn()
        );
    }
}
