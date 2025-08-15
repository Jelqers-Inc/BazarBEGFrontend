package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

public interface IUsuarioService {
    Page<Usuario> buscarTodosPaginados(String nombre, String rolNombre, Pageable pageable);

    List<Usuario> obtenerTodos();

    Optional<Usuario> buscarPorId(Integer id);

    Usuario crearOEditar(Usuario usuario);

    void eliminarPorId(Integer id);
}