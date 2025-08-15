package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPedidoService {
    Page<Pedido> buscarTodosPaginados(Pageable pageable);

    List<Pedido> obtenerTodos();

    Optional<Pedido> buscarPorId(Integer id);

    Pedido crearOEditar(Pedido pedido);

    void eliminarPorId(Integer id);
}