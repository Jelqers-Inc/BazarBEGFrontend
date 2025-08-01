package org.esfr.BazarBEG.repositorios;

import org.esfr.BazarBEG.modelos.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IUsuarioRepository extends JpaRepository<Usuario, Integer> {

    // Búsqueda personalizada por nombre y rol
    List<Usuario> findByNombreAndRol(String nombre, String rol);

}


