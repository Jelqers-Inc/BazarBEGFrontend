package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {
}
