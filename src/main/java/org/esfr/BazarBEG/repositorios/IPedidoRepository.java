package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    // Búsqueda con paginación
    Page<Pedido> findByFechaPedidoOrEstadoIgnoreCase(Date fechaPedido, String estado, Pageable pageable);

    @Query("SELECT p FROM Pedido p JOIN FETCH p.detalles JOIN FETCH p.usuario WHERE p.id = :id")
    Optional<Pedido> findByIdWithDetailsAndUser(@Param("id") Integer id);
}
