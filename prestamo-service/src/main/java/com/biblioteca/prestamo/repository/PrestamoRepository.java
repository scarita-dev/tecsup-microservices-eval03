package com.biblioteca.prestamo.repository;

import com.biblioteca.prestamo.model.EstadoPrestamo;
import com.biblioteca.prestamo.model.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrestamoRepository extends JpaRepository<Prestamo, Long> {

    List<Prestamo> findByEstado(EstadoPrestamo estado);

    List<Prestamo> findByLectorId(Long lectorId);

    List<Prestamo> findByLibroId(Long libroId);

    List<Prestamo> findByLectorIdAndEstado(Long lectorId, EstadoPrestamo estado);

    List<Prestamo> findByLibroIdAndEstado(Long libroId, EstadoPrestamo estado);
}
