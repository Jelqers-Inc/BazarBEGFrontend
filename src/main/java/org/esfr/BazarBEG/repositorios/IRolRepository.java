package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol, Integer> {

    // Buscar rol por nombre exacto
    Optional<Rol> findByNombre(String nombre);

    // Buscar rol único por nombre
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    // Paginación y búsqueda
    Page<Rol> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);


}
