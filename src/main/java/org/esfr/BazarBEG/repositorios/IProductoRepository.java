package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    // Buscar por nombre exacto
    List<Producto> findByNombre(String nombre);

    // Buscar por nombre ignorando mayúsculas y minúsculas
    List<Producto> findByNombreIgnoreCase(String nombre);

    // Buscar por nombre que contenga texto
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por categoría
    List<Producto> findByCategoria(Categoria categoria);

    // Buscar por nombre y categoría
    List<Producto> findByNombreAndCategoria(String nombre, Categoria categoria);

    // Buscar productos con stock igual a 0 (agotados)
    List<Producto> findByStockEquals(int cantidad);

    // Buscar por categoría (paginado)
    Page<Producto> findByCategoria(Categoria categoria, Pageable pageable);

}
