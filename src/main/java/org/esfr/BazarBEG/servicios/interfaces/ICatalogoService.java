package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.Catalogo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICatalogoService {
    Page<Catalogo> buscarTodosPaginados(Pageable pageable);

    List<Catalogo> obtenerTodos();

    Optional<Catalogo> buscarPorId(Integer id);

    Catalogo crearOEditar(Catalogo catalogo);

    void eliminarPorId(Integer id);
}
