package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUsuarioRepository extends JpaRepository<usuario, Integer> {

    // Búsqueda personalizada por nombre y rol
    List<usuario> findByNombreAndRol(String nombre, String rol);

}


