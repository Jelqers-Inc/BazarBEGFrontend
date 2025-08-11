package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Rol;
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
}
