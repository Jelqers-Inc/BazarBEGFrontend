package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPedidoService {
    Page<Pedido> buscarTodosPaginados(Pageable pageable);

    List<Pedido> obtenerTodos();

    Optional<Pedido> buscarPorId(Integer id);

    Pedido crearOEditar(Pedido pedido);

    void eliminarPorId(Integer id);

    Pedido crearPedido(Usuario usuario, Map<Producto, Integer> productos, double total);

    List<Pedido> obtenerPedidosPorUsuario(Usuario usuario);
}