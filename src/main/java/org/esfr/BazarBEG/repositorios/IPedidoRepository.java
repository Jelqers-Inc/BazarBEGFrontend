package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {
}
