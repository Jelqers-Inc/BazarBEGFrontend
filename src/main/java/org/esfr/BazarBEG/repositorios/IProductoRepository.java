package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    // Buscar por nombre exacto
    List<Producto> findByNombre(String nombre);

    // Buscar por nombre (ignorando mayúsculas/minúsculas)
    List<Producto> findByNombreIgnoreCase(String nombre);

    // Buscar por nombre que contenga texto (ideal para buscador)
    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por categoría
    List<Producto> findByCategoria(Categoria categoria);

    // Buscar por id de categoría
    List<Producto> findByCategoriaId(int categoriaId);

    // Buscar por nombre y categoría
    List<Producto> findByNombreAndCategoria(String nombre, Categoria categoria);

    // Buscar por nombre y id de categoría
    List<Producto> findByNombreAndCategoriaId(String nombre, int categoriaId);

    // Buscar por precio mayor o igual
    List<Producto> findByPrecioGreaterThanEqual(float precio);

    // Buscar por precio menor o igual
    List<Producto> findByPrecioLessThanEqual(float precio);

    // Buscar por rango de precio
    List<Producto> findByPrecioBetween(float min, float max);

    // Buscar productos con stock menor a cierto valor (alerta de inventario)
    List<Producto> findByStockLessThan(int cantidad);

    // Buscar productos con stock igual a 0 (agotados)
    List<Producto> findByStockEquals(int cantidad);

}
