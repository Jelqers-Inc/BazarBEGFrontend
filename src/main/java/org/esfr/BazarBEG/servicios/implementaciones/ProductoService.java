package org.esfr.BazarBEG.servicios.implementaciones;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product;
import org.esfr.BazarBEG.modelos.dtos.productos.ProductCreation;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.repositorios.IProductoRepository;
import org.esfr.BazarBEG.repositorios.ProductoRepository;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService; // Necesario para buscar el DTO
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private ProductoRepository productoRepository;



    @Override
    public Page<Product> buscarTodosPaginados(Pageable pageable) {
        List<Product> productos = productoRepository.obtenerTodas();

        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        List<Product> list;

        if (productos.size() < startItem) {
            list = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, productos.size());
            list = productos.subList(startItem, toIndex);


        }
        Page<Product> productPage = new PageImpl<>(list, pageable, productos.size());

        return productPage;
    }


    @Override
    public List<Product> obtenerTodos() {
        return productoRepository.obtenerTodas();
    }

    @Override
    public Product buscarPorId(Integer id) {
        return productoRepository.obtenerPorId(id);
    }

    @Override
    public ProductCreation crear(ProductCreation producto) {
        return productoRepository.crear(producto);
    }

    @Override
    public ProductCreation editar(ProductCreation producto) {
        return productoRepository.actualizar(producto);
    }

    @Override
    public void eliminarPorId(Integer id) {
        productoRepository.eliminar(id);
    }

    @Override
    public byte[] obtenerImagen(Integer id){
        return productoRepository.obtenerImagenPorId(id);
    }

}
