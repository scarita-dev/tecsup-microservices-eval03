package com.biblioteca.libro.service;

import com.biblioteca.libro.dto.LibroRequest;
import com.biblioteca.libro.dto.LibroResponse;
import com.biblioteca.libro.exception.BusinessException;
import com.biblioteca.libro.exception.DuplicateResourceException;
import com.biblioteca.libro.exception.ResourceNotFoundException;
import com.biblioteca.libro.model.EstadoLibro;
import com.biblioteca.libro.model.Libro;
import com.biblioteca.libro.repository.LibroRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LibroService {

    private final LibroRepository libroRepository;

    public LibroService(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Transactional(readOnly = true)
    public List<LibroResponse> findAll() {
        return libroRepository.findAll().stream()
                .map(LibroResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibroResponse findById(Long id) {
        return LibroResponse.from(getById(id));
    }

    @Transactional
    public LibroResponse create(LibroRequest request) {
        String isbn = normalizeIsbn(request.isbn());
        if (libroRepository.existsByIsbn(isbn)) {
            throw new DuplicateResourceException("Ya existe un libro registrado con el ISBN " + isbn);
        }

        Libro libro = new Libro(
                isbn,
                request.titulo().trim(),
                request.autor().trim(),
                request.stock(),
                request.estado()
        );
        if (libro.getStock() == 0 && libro.getEstado() == EstadoLibro.DISPONIBLE) {
            libro.setEstado(EstadoLibro.NO_DISPONIBLE);
        }
        return LibroResponse.from(libroRepository.save(libro));
    }

    @Transactional
    public LibroResponse update(Long id, LibroRequest request) {
        Libro libro = getById(id);
        String isbn = normalizeIsbn(request.isbn());

        libroRepository.findByIsbn(isbn)
                .filter(found -> !found.getId().equals(id))
                .ifPresent(found -> {
                    throw new DuplicateResourceException("El ISBN " + isbn + " pertenece a otro libro");
                });

        libro.setIsbn(isbn);
        libro.setTitulo(request.titulo().trim());
        libro.setAutor(request.autor().trim());
        libro.setStock(request.stock());
        libro.setEstado(request.estado());

        if (libro.getStock() == 0 && libro.getEstado() == EstadoLibro.DISPONIBLE) {
            libro.setEstado(EstadoLibro.NO_DISPONIBLE);
        }
        return LibroResponse.from(libroRepository.save(libro));
    }

    @Transactional
    public void delete(Long id) {
        Libro libro = getById(id);
        libroRepository.delete(libro);
    }

    @Transactional
    public LibroResponse reservar(Long id) {
        Libro libro = getByIdForUpdate(id);
        if (libro.getEstado() != EstadoLibro.DISPONIBLE) {
            throw new BusinessException("El libro no está disponible para préstamo; estado actual: " + libro.getEstado());
        }
        if (libro.getStock() == null || libro.getStock() <= 0) {
            libro.setEstado(EstadoLibro.NO_DISPONIBLE);
            libroRepository.save(libro);
            throw new BusinessException("El libro no tiene stock disponible");
        }

        libro.setStock(libro.getStock() - 1);
        if (libro.getStock() == 0) {
            libro.setEstado(EstadoLibro.NO_DISPONIBLE);
        }
        return LibroResponse.from(libroRepository.save(libro));
    }

    @Transactional
    public LibroResponse liberar(Long id) {
        Libro libro = getByIdForUpdate(id);
        int currentStock = libro.getStock() == null ? 0 : libro.getStock();
        libro.setStock(currentStock + 1);
        libro.setEstado(EstadoLibro.DISPONIBLE);
        return LibroResponse.from(libroRepository.save(libro));
    }

    private Libro getById(Long id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un libro con el ID " + id));
    }

    private Libro getByIdForUpdate(Long id) {
        return libroRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un libro con el ID " + id));
    }

    private String normalizeIsbn(String isbn) {
        return isbn == null ? null : isbn.trim().toUpperCase();
    }
}
