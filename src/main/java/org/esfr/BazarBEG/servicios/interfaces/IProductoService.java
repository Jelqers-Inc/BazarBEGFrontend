package org.esfr.BazarBEG.servicios.interfaces;

import org.esfr.BazarBEG.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductoService {
    Page<Producto> buscarTodosPaginados(Pageable pageable);

    List<Producto> obtenerTodos();

    Optional<Producto> buscarPorId(Integer id);

    Producto crearOEditar(Producto producto);

    void eliminarPorId(Integer id);

    Page<Producto> buscarPorFiltroPaginado(String nombre, String categoriaNombre, Pageable pageable);

    List<Producto> buscarPorIds(List<Integer> productosIds);

    List<Producto> findByCategoriaId(Integer categoriaId);

    List<Producto> obtenerProductosActivos();

    List<Producto> obtenerProductosPorCategoriaActivos(Long categoriaId);

    Optional<Producto> obtenerProductoActivoPorId(Long id);

    List<Producto> buscarProductosActivos(String q);
    List<Producto> buscarProductosPorCategoriaActivos(Long categoriaId, String q);

}
