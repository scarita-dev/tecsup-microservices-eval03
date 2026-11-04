package com.biblioteca.lector.repository;

import com.biblioteca.lector.model.Lector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LectorRepository extends JpaRepository<Lector, Long> {

    Optional<Lector> findByDocumento(String documento);

    boolean existsByDocumento(String documento);
}
