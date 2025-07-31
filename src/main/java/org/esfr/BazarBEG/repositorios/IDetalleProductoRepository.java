package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDetalleProductoRepository extends JpaRepository<DetallePedido, Integer> {
}
