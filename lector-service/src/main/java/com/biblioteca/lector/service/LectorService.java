package com.biblioteca.lector.service;

import com.biblioteca.lector.dto.LectorRequest;
import com.biblioteca.lector.dto.LectorResponse;
import com.biblioteca.lector.exception.DuplicateResourceException;
import com.biblioteca.lector.exception.ResourceNotFoundException;
import com.biblioteca.lector.model.Lector;
import com.biblioteca.lector.repository.LectorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LectorService {

    private final LectorRepository lectorRepository;

    public LectorService(LectorRepository lectorRepository) {
        this.lectorRepository = lectorRepository;
    }

    @Transactional(readOnly = true)
    public List<LectorResponse> findAll() {
        return lectorRepository.findAll().stream()
                .map(LectorResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LectorResponse findById(Long id) {
        return LectorResponse.from(getById(id));
    }

    @Transactional(readOnly = true)
    public LectorResponse findByDocumento(String documento) {
        return lectorRepository.findByDocumento(normalizeDocumento(documento))
                .map(LectorResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un lector con el documento " + documento));
    }

    @Transactional
    public LectorResponse create(LectorRequest request) {
        String documento = normalizeDocumento(request.documento());
        if (lectorRepository.existsByDocumento(documento)) {
            throw new DuplicateResourceException("Ya existe un lector registrado con el documento " + documento);
        }

        Lector lector = new Lector(
                documento,
                request.nombres().trim(),
                request.contacto().trim()
        );
        return LectorResponse.from(lectorRepository.save(lector));
    }

    @Transactional
    public LectorResponse update(Long id, LectorRequest request) {
        Lector lector = getById(id);
        String documento = normalizeDocumento(request.documento());

        lectorRepository.findByDocumento(documento)
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new DuplicateResourceException("El documento " + documento + " pertenece a otro lector");
                });

        lector.setDocumento(documento);
        lector.setNombres(request.nombres().trim());
        lector.setContacto(request.contacto().trim());
        return LectorResponse.from(lectorRepository.save(lector));
    }

    @Transactional
    public void delete(Long id) {
        Lector lector = getById(id);
        lectorRepository.delete(lector);
    }

    private Lector getById(Long id) {
        return lectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un lector con el ID " + id));
    }

    private String normalizeDocumento(String documento) {
        return documento == null ? null : documento.trim().toUpperCase();
    }
}
