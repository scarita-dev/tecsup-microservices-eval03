package com.biblioteca.prestamo.controller;

import com.biblioteca.prestamo.dto.PrestamoRequest;
import com.biblioteca.prestamo.dto.PrestamoResponse;
import com.biblioteca.prestamo.dto.PrestamoUpdateRequest;
import com.biblioteca.prestamo.model.EstadoPrestamo;
import com.biblioteca.prestamo.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public List<PrestamoResponse> findAll(
            @RequestParam(required = false) Long lectorId,
            @RequestParam(required = false) Long libroId,
            @RequestParam(required = false) EstadoPrestamo estado) {
        return prestamoService.findAll(lectorId, libroId, estado);
    }

    @GetMapping("/{id}")
    public PrestamoResponse findById(@PathVariable Long id) {
        return prestamoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PrestamoResponse create(@Valid @RequestBody PrestamoRequest request) {
        return prestamoService.create(request);
    }

    @PutMapping("/{id}")
    public PrestamoResponse update(@PathVariable Long id, @Valid @RequestBody PrestamoUpdateRequest request) {
        return prestamoService.update(id, request);
    }

    @PutMapping("/{id}/devolucion")
    public PrestamoResponse devolver(@PathVariable Long id) {
        return prestamoService.devolver(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        prestamoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
