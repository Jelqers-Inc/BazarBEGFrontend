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

    // Buscar por nombre ignorando mayúsculas/minúsculas
    List<Usuario> findByNombreIgnoreCase(String nombre);

    // Buscar por nombre que contenga texto
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por ID (ya existe en JpaRepository, pero lo puedes declarar explícito si quieres)
    Optional<Usuario> findById(int id);

    // Buscar por rol
    List<Usuario> findByRol(Rol rol);

    // Buscar por id de rol
    List<Usuario> findByRolId(int rolId);

    // Buscar por nombre y rol
    List<Usuario> findByNombreAndRol(String nombre, Rol rol);

    // Buscar por nombre y id de rol
    List<Usuario> findByNombreAndRolId(String nombre, int rolId);

    // Buscar por id y rol
    Optional<Usuario> findByIdAndRol(int id, Rol rol);

    // Buscar por id y id de rol
    Optional<Usuario> findByIdAndRolId(int id, int rolId);
}


