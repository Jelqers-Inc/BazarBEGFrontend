package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.repositorios.IUsuarioRepository;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public Usuario crear(Usuario usuario) {
        return null;
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        return null;
    }

    @Override
    public void eliminar(int id) {

    }

    @Override
    public Usuario obtenerPorId(int id) {
        return null;
    }

    @Override
    public Usuario registrar(Usuario usuario) {
        return null;
    }

    @Override
    public Usuario iniciarSesion(String email, String contrasena) {
        return null;
    }

    @Override
    public List<Pedido> verHistorialPedidos(int idUsuario) {
        return List.of();
    }
}
