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

    // Buscar rol por nombre ignorando mayúsculas y minúsculas
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    // Buscar roles
    List<Rol> findByNombreContainingIgnoreCase(String nombre);

    Page<Rol> findAll(Pageable pageable);

    // Buscar rol por nombre que contenga texto con paginación
    Page<Rol> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Buscar rol cuyo nombre empiece con texto dado, con paginación
    Page<Rol> findByNombreStartingWithIgnoreCase(String prefijo, Pageable pageable);


}
