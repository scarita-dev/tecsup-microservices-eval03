package com.biblioteca.prestamo.service;

import com.biblioteca.prestamo.client.LectorClient;
import com.biblioteca.prestamo.client.LibroClient;
import com.biblioteca.prestamo.dto.LectorResumen;
import com.biblioteca.prestamo.dto.PrestamoRequest;
import com.biblioteca.prestamo.dto.PrestamoResponse;
import com.biblioteca.prestamo.dto.PrestamoUpdateRequest;
import com.biblioteca.prestamo.exception.BusinessException;
import com.biblioteca.prestamo.exception.ResourceNotFoundException;
import com.biblioteca.prestamo.model.EstadoPrestamo;
import com.biblioteca.prestamo.model.Prestamo;
import com.biblioteca.prestamo.repository.PrestamoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private static final Logger log = LoggerFactory.getLogger(PrestamoService.class);

    private final PrestamoRepository prestamoRepository;
    private final LectorClient lectorClient;
    private final LibroClient libroClient;

    public PrestamoService(PrestamoRepository prestamoRepository, LectorClient lectorClient, LibroClient libroClient) {
        this.prestamoRepository = prestamoRepository;
        this.lectorClient = lectorClient;
        this.libroClient = libroClient;
    }

    @Transactional(readOnly = true)
    public List<PrestamoResponse> findAll(Long lectorId, Long libroId, EstadoPrestamo estado) {
        List<Prestamo> prestamos;
        if (lectorId != null && estado != null) {
            prestamos = prestamoRepository.findByLectorIdAndEstado(lectorId, estado);
        } else if (libroId != null && estado != null) {
            prestamos = prestamoRepository.findByLibroIdAndEstado(libroId, estado);
        } else if (lectorId != null) {
            prestamos = prestamoRepository.findByLectorId(lectorId);
        } else if (libroId != null) {
            prestamos = prestamoRepository.findByLibroId(libroId);
        } else if (estado != null) {
            prestamos = prestamoRepository.findByEstado(estado);
        } else {
            prestamos = prestamoRepository.findAll();
        }
        return prestamos.stream()
                .map(PrestamoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponse findById(Long id) {
        return PrestamoResponse.from(getById(id));
    }

    @Transactional
    public PrestamoResponse create(PrestamoRequest request) {
        validateDateRange(request.fechaPrestamo(), request.fechaLimite());

        LectorResumen lector = lectorClient.obtenerPorId(request.lectorId());
        log.info("Registrando préstamo para lector {} ({})", lector.id(), lector.documento());

        libroClient.reservar(request.libroId());
        try {
            Prestamo prestamo = new Prestamo(
                    request.lectorId(),
                    request.libroId(),
                    request.fechaPrestamo(),
                    request.fechaLimite()
            );
            return PrestamoResponse.from(prestamoRepository.save(prestamo));
        } catch (RuntimeException ex) {
            compensate("liberar", request.libroId(), ex);
            throw ex;
        }
    }

    @Transactional
    public PrestamoResponse update(Long id, PrestamoUpdateRequest request) {
        Prestamo prestamo = getById(id);
        ensureActive(prestamo, "modificar");
        validateDateRange(prestamo.getFechaPrestamo(), request.fechaLimite());

        prestamo.setFechaLimite(request.fechaLimite());
        return PrestamoResponse.from(prestamoRepository.save(prestamo));
    }

    @Transactional
    public PrestamoResponse devolver(Long id) {
        Prestamo prestamo = getById(id);
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new BusinessException("El préstamo ya fue devuelto el " + prestamo.getFechaDevolucion());
        }
        if (prestamo.getEstado() == EstadoPrestamo.CANCELADO) {
            throw new BusinessException("El préstamo fue cancelado y no puede devolverse");
        }

        libroClient.liberar(prestamo.getLibroId());
        try {
            prestamo.setFechaDevolucion(LocalDate.now());
            prestamo.setEstado(EstadoPrestamo.DEVUELTO);
            return PrestamoResponse.from(prestamoRepository.save(prestamo));
        } catch (RuntimeException ex) {
            compensate("reservar", prestamo.getLibroId(), ex);
            throw ex;
        }
    }

    @Transactional
    public void delete(Long id) {
        Prestamo prestamo = getById(id);
        boolean wasActive = prestamo.getEstado() == EstadoPrestamo.ACTIVO;

        if (wasActive) {
            libroClient.liberar(prestamo.getLibroId());
        }

        try {
            prestamoRepository.delete(prestamo);
        } catch (RuntimeException ex) {
            if (wasActive) {
                compensate("reservar", prestamo.getLibroId(), ex);
            }
            throw ex;
        }
    }

    private Prestamo getById(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un préstamo con el ID " + id));
    }

    private void validateDateRange(LocalDate fechaPrestamo, LocalDate fechaLimite) {
        if (fechaLimite.isBefore(fechaPrestamo)) {
            throw new BusinessException("La fecha límite no puede ser anterior a la fecha de préstamo");
        }
    }

    private void ensureActive(Prestamo prestamo, String action) {
        if (prestamo.getEstado() != EstadoPrestamo.ACTIVO) {
            throw new BusinessException("Solo se puede " + action + " un préstamo ACTIVO; estado actual: " + prestamo.getEstado());
        }
    }

    private void compensate(String operation, Long libroId, RuntimeException original) {
        try {
            if ("liberar".equals(operation)) {
                libroClient.liberar(libroId);
            } else if ("reservar".equals(operation)) {
                libroClient.reservar(libroId);
            }
            log.warn("Compensación '{}' ejecutada para libro {} tras error: {}", operation, libroId, original.getMessage());
        } catch (RuntimeException compensation) {
            log.error("No se pudo ejecutar la compensación '{}' para libro {}: {}", operation, libroId, compensation.getMessage(), compensation);
        }
    }
}
