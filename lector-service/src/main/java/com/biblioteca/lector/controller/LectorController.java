package com.biblioteca.lector.controller;

import com.biblioteca.lector.dto.LectorRequest;
import com.biblioteca.lector.dto.LectorResponse;
import com.biblioteca.lector.service.LectorService;
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
@RequestMapping("/api/lectores")
public class LectorController {

    private final LectorService lectorService;

    public LectorController(LectorService lectorService) {
        this.lectorService = lectorService;
    }

    @GetMapping
    public List<LectorResponse> findAll() {
        return lectorService.findAll();
    }

    @GetMapping("/{id}")
    public LectorResponse findById(@PathVariable Long id) {
        return lectorService.findById(id);
    }

    @GetMapping("/documento/{documento}")
    public LectorResponse findByDocumento(@PathVariable String documento) {
        return lectorService.findByDocumento(documento);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LectorResponse create(@Valid @RequestBody LectorRequest request) {
        return lectorService.create(request);
    }

    @PutMapping("/{id}")
    public LectorResponse update(@PathVariable Long id, @Valid @RequestBody LectorRequest request) {
        return lectorService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lectorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
