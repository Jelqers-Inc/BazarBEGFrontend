package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Catalogo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Búsqueda por nombre con paginación
    Page<Catalogo> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // Búsqueda de catálogos por categoria
    List<Catalogo> findByCategoriaId(Integer categoriaId);

    // Nueva consulta para cargar el catálogo y sus productos
    @Query("SELECT c FROM Catalogo c LEFT JOIN FETCH c.productos WHERE c.id = :id")
    Optional<Catalogo> findByIdWithProducts(@Param("id") Integer id);

    @Query("SELECT c FROM Catalogo c LEFT JOIN FETCH c.productos p LEFT JOIN FETCH c.categoria cat WHERE c.id = :id")
    Optional<Catalogo> findByIdWithDetails(@Param("id") Integer id);


}
