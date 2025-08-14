package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Buscar por nombre exacto
    List<Usuario> findByNombre(String nombre);

    // Buscar por nombre ignorando mayúsculas y minúsculas
    List<Usuario> findByNombreIgnoreCase(String nombre);

    // Buscar por nombre que contenga texto y por rol
    List<Usuario> findByNombreContainingIgnoreCaseAndRol(String nombre, Rol rol);

    // Buscar por ID
    Optional<Usuario> findById(int id);

    // Buscar por nombre y rol
    List<Usuario> findByNombreAndRol(String nombre, Rol rol);

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Paginación con búsqueda combinada
    Page<Usuario> findByNombreContainingIgnoreCaseOrRol_NombreContainingIgnoreCase(
            String nombre, String rolNombre, Pageable pageable
    );
}

