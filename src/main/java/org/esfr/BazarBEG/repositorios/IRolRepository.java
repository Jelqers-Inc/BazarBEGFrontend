package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IRolRepository extends JpaRepository<Rol, Integer> {
    // Buscar rol por nombre exacto
    Optional<Rol> findByNombre(String nombre);

    // Buscar rol por nombre ignorando mayúsculas/minúsculas
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    // Buscar roles cuyo nombre contenga texto (útil si hay más de uno, como ADMIN, SUPERADMIN, etc.)
    List<Rol> findByNombreContainingIgnoreCase(String nombre);
}
