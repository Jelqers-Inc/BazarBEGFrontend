package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProductoRepository extends JpaRepository<Producto, Integer> {

    // Búsqueda personalizada por nombre
    List<Producto> findByNombre(String nombre);

}
