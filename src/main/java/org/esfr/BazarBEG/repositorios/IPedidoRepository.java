package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {

    // Buscar por fecha exacta
    List<Pedido> findByFechaPedido(Date fechaPedido);

    // Buscar por estado exacto
    List<Pedido> findByEstado(String estado);

    // Buscar por estado ignorando mayúsculas y minúsculas
    List<Pedido> findByEstadoIgnoreCase(String estado);

    // Buscar por fecha y estado exactos
    List<Pedido> findByFechaPedidoAndEstado(Date fechaPedido, String estado);

    // Buscar por fecha y estado ignorando mayúsculas y minúsculas
    List<Pedido> findByFechaPedidoAndEstadoIgnoreCase(Date fechaPedido, String estado);
}
