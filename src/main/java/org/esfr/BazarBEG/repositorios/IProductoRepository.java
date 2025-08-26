package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    // Búsqueda combinada con paginación
    Page<Producto> findByNombreContainingIgnoreCaseOrCategoria_NombreContainingIgnoreCase(
            String nombre, String categoriaNombre, Pageable pageable
    );

    // Buscar productos con stock igual a 0 (agotados)
    Page<Producto> findByStockEquals(int cantidad, Pageable pageable);

    List<Producto> findByIdIn(List<Integer> ids);

    List<Producto> findByCategoriaId(Integer categoriaId);

    List<Producto> findByStatus(int status);

    // Encuentra productos por el ID de la categoría y que estén activos (status = 1)
    List<Producto> findByCategoriaIdAndStatus(Long categoriaId, int status);

    // Encuentra un producto por su ID y que esté activo (status = 1)
    Optional<Producto> findByIdAndStatus(Long id, int status);
}
