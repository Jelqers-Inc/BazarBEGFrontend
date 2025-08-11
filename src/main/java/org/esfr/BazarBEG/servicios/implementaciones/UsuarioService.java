package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.repositorios.IUsuarioRepository;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario crear(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public Usuario actualizar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminar(int id) {
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario obtenerPorId(int id) {
        return usuarioRepository.findById(id).orElse(null);
    }

//        @Override
//        public Usuario registrar(Usuario usuario) {
//
//            String contraseñaEncriptada = passwordEncoder.encode(usuario.getContraseña());
//            usuario.setContraseña(contraseñaEncriptada);
//
//            usuario.setFechaRegistro(new Date());
//            return usuarioRepository.save(usuario);

//}
//        @Override
//        public Usuario iniciarSesion(String email, String contrasena) {
//            Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);
//            if (usuarioOptional.isPresent()) {
//                Usuario usuario = usuarioOptional.get();
//                if (passwordEncoder.matches(contrasena, usuario.getContraseña())) {
//                    return usuario;
//                }
//            }
//
//            return null;
//
//        }

    @Override
    public List<Pedido> verHistorialPedidos(int idUsuario) {
        return List.of();
    }


    @Override
    public Page<Usuario> obtenerTodosPaginados(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

}