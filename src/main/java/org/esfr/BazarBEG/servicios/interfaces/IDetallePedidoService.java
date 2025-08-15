package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.DetallePedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDetallePedidoService {
Page<DetallePedido> buscarTodosPaginados(Pageable pageable);

    List<DetallePedido> obtenerTodos();

    Optional<DetallePedido> buscarPorId(Integer id);

    DetallePedido crearOEditar(DetallePedido detallePedido);

    void eliminarPorId(Integer id);
}
