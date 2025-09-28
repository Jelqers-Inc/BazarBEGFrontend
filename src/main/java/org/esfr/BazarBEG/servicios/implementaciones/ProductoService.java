package org.esfr.BazarBEG.servicios.implementaciones;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product;
import org.esfr.BazarBEG.repositorios.IProductoRepository;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService; // Necesario para buscar el DTO
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto; // Usamos el DTO correcto

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private IProductoRepository productoRepository;

    @Autowired
    private ICategoriaService categoriaService; // Inyectamos el servicio para obtener los DTOs de Categoría

    /**
     * Mapea una entidad Producto a su DTO Product, inyectando el DTO de Categoría.
     */
    private Product mapToProductDto(Producto producto) {
        if (producto == null) return null;

        Product dto = new Product();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setImagen(producto.getImagen());
        dto.setStatus(producto.getStatus());

        // **Parte CRÍTICA:** Obtener el DTO de Categoría de la API externa
        if (producto.getCategoria() != null) {
            // Buscamos el DTO real de Categoría y lo asignamos
            categoriaService.buscarPorId(producto.getCategoria().getId())
                    .ifPresent(categoria -> dto.setCategoria(categoria));
        }

        return dto;
    }

    private Page<Product> mapToProductDtoPage(Page<Producto> productoPage) {
        if (productoPage == null) return Page.empty();

        // Mapea el contenido del Page usando el método mapToProductDto
        List<Product> dtos = productoPage.getContent().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());

        // Crea un nuevo Page a partir de la lista de DTOs y la metadata original
        return new PageImpl<>(dtos, productoPage.getPageable(), productoPage.getTotalElements());
    }

    // Solución al error: Spring Boot 3 requiere la implementación del método hasContent()
    private static class PageImpl<T> implements Page<T> {
        private final List<T> content;
        private final Pageable pageable;
        private final long totalElements;

        public PageImpl(List<T> content, Pageable pageable, long totalElements) {
            this.content = content;
            this.pageable = pageable;
            this.totalElements = totalElements;
        }

        @Override public List<T> getContent() { return content; }
        @Override public Pageable getPageable() { return pageable; }

        @Override
        public Pageable nextPageable() {
            return null;
        }

        @Override
        public Pageable previousPageable() {
            return null;
        }

        @Override public long getTotalElements() { return totalElements; }
        // Implementación de Page/Slice
        @Override public int getTotalPages() { return pageable.getPageSize() == 0 ? 1 : (int) Math.ceil((double) totalElements / pageable.getPageSize()); }
        @Override public int getNumber() { return pageable.getPageNumber(); }
        @Override public int getSize() { return pageable.getPageSize(); }
        @Override public int getNumberOfElements() { return content.size(); }
        @Override public boolean hasPrevious() { return getNumber() > 0; }
        @Override public boolean isFirst() { return getNumber() == 0; }
        @Override public boolean isLast() { return getNumber() == (getTotalPages() - 1); }
        @Override public boolean hasNext() { return getNumber() < (getTotalPages() - 1); }

        // ** MÉTODO FALTANTE CORREGIDO **
        @Override public boolean hasContent() { return !content.isEmpty(); }

        @Override
        public Sort getSort() {
            return null;
        }

        @Override public <U> Page<U> map(java.util.function.Function<? super T, ? extends U> converter) {
            throw new UnsupportedOperationException("Not implemented");
        }
        @Override public java.util.Iterator<T> iterator() { return content.iterator(); }
    }


    @Override
    public Page<Product> buscarTodosPaginados(Pageable pageable) {
        Page<Producto> productos = productoRepository.findAll(pageable);
        return mapToProductDtoPage(productos);
    }

    @Override
    public Page<Product> buscarPorFiltroPaginado(String nombre, String categoriaNombre, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByNombreContainingIgnoreCaseOrCategoria_NombreContainingIgnoreCase(
                nombre, categoriaNombre, pageable
        );
        return mapToProductDtoPage(productos);
    }

    @Override
    public List<Product> obtenerTodos() {
        return productoRepository.findAll().stream()
                .map(this::mapToProductDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Product> buscarPorId(Integer id) {
        return productoRepository.findById(id)
                .map(this::mapToProductDto);
    }

    // Los métodos de persistencia siguen usando la entidad Producto
    @Override
    public Producto crearOEditar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void eliminarPorId(Integer id) {
        productoRepository.deleteById(id);
    }

    // Métodos que devuelven la entidad Producto (para uso interno o casos específicos)
    @Override
    public List<Producto> buscarPorIds(List<Integer> ids) {
        return productoRepository.findByIdIn(ids);
    }

    @Override
    public List<Producto> findByCategoriaId(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

    public List<Producto> obtenerProductosActivos() {
        return productoRepository.findByStatus(1);
    }

    public List<Producto> obtenerProductosPorCategoriaActivos(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndStatus(categoriaId, 1);
    }

    public Optional<Producto> obtenerProductoActivoPorId(Long id) {
        return productoRepository.findByIdAndStatus(id, 1);
    }

    @Override
    public List<Producto> buscarProductosActivos(String q) {
        return productoRepository.findByNombreContainingIgnoreCaseAndStatus(q, 1);
    }

    @Override
    public List<Producto> buscarProductosPorCategoriaActivos(Long categoriaId, String q) {
        return productoRepository.findByCategoriaIdAndNombreContainingIgnoreCaseAndStatus(categoriaId, q, 1);
    }
}
