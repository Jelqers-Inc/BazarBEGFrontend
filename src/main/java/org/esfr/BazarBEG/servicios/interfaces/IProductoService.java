package org.esfr.BazarBEG.servicios.interfaces;

import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product; // Importamos el DTO de Producto
import org.esfr.BazarBEG.modelos.dtos.productos.ProductCreation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IProductoService {
    // Los listados y búsquedas ahora devuelven DTOs (Product)
    Page<Product> buscarTodosPaginados(Pageable pageable);
    List<Product> obtenerTodos();
    Product buscarPorId(Integer id);
    byte[] obtenerImagen(Integer id);
    //Page<Product> buscarPorFiltroPaginado(String nombre, String categoriaNombre, Pageable pageable);

    // La creación/edición aún usa la entidad Producto para interactuar con el IProductoRepository JPA
    ProductCreation crear(ProductCreation producto);
    ProductCreation editar(ProductCreation producto);

    void eliminarPorId(Integer id);


    // Métodos que devuelven entidades (pueden ser usados internamente o por el carrito/detalles)
//    List<Producto> buscarPorIds(List<Integer> productosIds);
//    List<Producto> findByCategoriaId(Integer categoriaId);
//    List<Producto> obtenerProductosActivos();
//    List<Producto> obtenerProductosPorCategoriaActivos(Long categoriaId);
//    Optional<Producto> obtenerProductoActivoPorId(Long id);
//    List<Producto> buscarProductosActivos(String q);
//    List<Producto> buscarProductosPorCategoriaActivos(Long categoriaId, String q);

}
