package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.repositorios.IUsuarioRepository;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public Page<Usuario> buscarTodosPaginados(String nombre, String rolNombre, Pageable pageable) {
        if (nombre.isBlank() && rolNombre.isBlank()) {
            return usuarioRepository.findAll(pageable);
        } else if (rolNombre.isBlank()) {
            return usuarioRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        } else {
            return usuarioRepository.findByNombreContainingIgnoreCaseOrRol_NombreContainingIgnoreCase(
                    nombre, rolNombre, pageable);
        }
    }

    @Override
    public Page<Usuario> obtenerTodosPaginados(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Override
    public Page<Usuario> buscarPorTermino(String termino, Pageable pageable) {
        return usuarioRepository.findByNombreContainingIgnoreCaseOrRol_NombreContainingIgnoreCase(
                termino, termino, pageable);
    }

    @Override
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public Usuario crearOEditar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarPorId(Integer id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

}
