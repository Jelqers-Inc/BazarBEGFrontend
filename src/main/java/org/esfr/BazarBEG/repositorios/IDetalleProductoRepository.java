package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.DetallePedido;
import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.modelos.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IDetalleProductoRepository extends JpaRepository<DetallePedido, Integer> {

    // Buscar todos los detalles de un pedido específico
    List<DetallePedido> findByPedido(Pedido pedido);

    // Buscar todos los detalles por ID del pedido
    List<DetallePedido> findByPedidoId(int pedidoId);

    // Buscar por producto
    List<DetallePedido> findByProducto(Producto producto);

    // Buscar por pedido y producto
    Optional<DetallePedido> findByPedidoAndProducto(Pedido pedido, Producto producto);

    // Buscar por ID de pedido y producto
    Optional<DetallePedido> findByPedidoIdAndProductoId(int pedidoId, int productoId);

    // --- Paginación con filtros opcionales ---
    @Query("""
        SELECT d FROM DetallePedido d
        WHERE (:pedidoId IS NULL OR d.pedido.id = :pedidoId)
          AND (:cantidad IS NULL OR d.cantidad = :cantidad)
          AND (:precioUnitario IS NULL OR d.precioUnitario = :precioUnitario)
    """)
    Page<DetallePedido> buscarConFiltros(
            @Param("pedidoId") Integer pedidoId,
            @Param("cantidad") Integer cantidad,
            @Param("precioUnitario") Double precioUnitario,
            Pageable pageable
    );
}
