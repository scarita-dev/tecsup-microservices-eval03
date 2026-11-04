package com.biblioteca.libro.controller;

import com.biblioteca.libro.dto.LibroRequest;
import com.biblioteca.libro.dto.LibroResponse;
import com.biblioteca.libro.service.LibroService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {

    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public List<LibroResponse> findAll() {
        return libroService.findAll();
    }

    @GetMapping("/{id}")
    public LibroResponse findById(@PathVariable Long id) {
        return libroService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LibroResponse create(@Valid @RequestBody LibroRequest request) {
        return libroService.create(request);
    }

    @PutMapping("/{id}")
    public LibroResponse update(@PathVariable Long id, @Valid @RequestBody LibroRequest request) {
        return libroService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        libroService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reservar")
    public LibroResponse reservar(@PathVariable Long id) {
        return libroService.reservar(id);
    }

    @PostMapping("/{id}/liberar")
    public LibroResponse liberar(@PathVariable Long id) {
        return libroService.liberar(id);
    }
}
