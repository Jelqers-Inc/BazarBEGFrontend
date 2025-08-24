package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Catalogo;
import org.esfr.BazarBEG.repositorios.ICatalogoRepository;
import org.esfr.BazarBEG.servicios.interfaces.ICatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogoService implements ICatalogoService {

    @Autowired
    private ICatalogoRepository catalogoRepository;

    @Override
    public Page<Catalogo> buscarTodosPaginados(Pageable pageable) {
        return catalogoRepository.findAll(pageable);
    }

    @Override
    public List<Catalogo> obtenerTodos() {
        return catalogoRepository.findAll();
    }

    @Override
    public Catalogo crearOEditar(Catalogo catalogo) {
        return catalogoRepository.save(catalogo);
    }

    @Override
    public void eliminarPorId(Integer id) {
        catalogoRepository.deleteById(id);
    }

    @Override
    public Optional<Catalogo> buscarPorId(Integer id) {
        // Usa la nueva consulta para asegurar que los productos se cargan
        return catalogoRepository.findByIdWithProducts(id);
    }

    @Override
    public Optional<Catalogo> buscarPorIdy(Integer id) {
        return catalogoRepository.findByIdWithDetails(id);
    }

    @Override
    public Page<Catalogo> buscarPorNombrePaginado(String nombre, Pageable pageable) {
        return catalogoRepository.findByNombreContainingIgnoreCase(nombre, pageable);
    }
}
