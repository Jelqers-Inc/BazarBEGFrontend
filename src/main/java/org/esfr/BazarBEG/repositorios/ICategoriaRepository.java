package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {

    // Buscar por nombre
    List<Categoria> findByNombre(String nombre);

    // Buscar por nombre ignorando mayúsculas y minúsculas
    List<Categoria> findByNombreIgnoreCase(String nombre);

    // Búsqueda con paginación
    Page<Categoria> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);


}
