package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.repositorios.IProductoRepository;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

@Service
public class ProductoService implements IProductoService {

    @Autowired
    private IProductoRepository productoRepository;


    public Page<Producto> buscarTodosPaginados(String nombre, String categoriaNombre, Pageable pageable) {
        return productoRepository.findByNombreContainingIgnoreCaseOrCategoria_NombreContainingIgnoreCase(
                nombre, categoriaNombre, pageable
        );
    }

    @Override
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> buscarPorId(Integer id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto crearOEditar(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void eliminarPorId(Integer id) {
        productoRepository.deleteById(id);
    }

    @Override
    public Page<Producto> buscarTodosPaginados(Pageable pageable) {
        return productoRepository.findAll(pageable);
    }

    @Override
    public List<Producto> buscarPorIds(List<Integer> ids) {
        return productoRepository.findByIdIn(ids);
    }

    @Override
    public List<Producto> findByCategoriaId(Integer categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId);
    }

}
