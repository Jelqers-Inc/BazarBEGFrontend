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
    public Optional<Catalogo> buscarPorId(Integer id) {
        return catalogoRepository.findById(id);
    }

    @Override
    public Catalogo crearOEditar(Catalogo catalogo) {
        return catalogoRepository.save(catalogo);
    }

    @Override
    public void eliminarPorId(Integer id) {
        catalogoRepository.deleteById(id);
    }
    
}
