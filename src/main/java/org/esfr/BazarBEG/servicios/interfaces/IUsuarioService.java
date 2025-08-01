package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.Pedido;

public interface IUsuarioService {

    // Métodos CRUD (crear, actualizar, eliminar, obtener)
    Usuario crear(Usuario usuario);
    Usuario actualizar(Usuario usuario);
    void eliminar(int id);
    Usuario obtenerPorId(int id);

    // Métodos específicos de la clase Usuario
    Usuario registrar(Usuario usuario);
    Usuario iniciarSesion(String email, String contrasena);
    List<Pedido> verHistorialPedidos(int idUsuario);
}