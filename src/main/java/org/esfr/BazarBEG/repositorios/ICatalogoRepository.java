package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Catalogo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICatalogoRepository extends JpaRepository<Catalogo, Integer> {

    // Buscar por nombre
    List<Catalogo> findByNombre(String nombre);

    // Buscar por nombre ignorando mayúsculas y minúsculas
    List<Catalogo> findByNombreIgnoreCase(String nombre);

    // Buscar por nombre que contenga texto
    List<Catalogo> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por id
    Optional<Catalogo> findById(int id);

    // Buscar por nombre e id exactos
    Optional<Catalogo> findByNombreAndId(String nombre, int id);

    // Paginación general
    Page<Catalogo> findAll(Pageable pageable);
}
