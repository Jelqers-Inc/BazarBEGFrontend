package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Catalogo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICatalogoRepository extends JpaRepository<Catalogo, Integer> {
}
