package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.dtos.usuarios.LoginResponse;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.modelos.dtos.usuarios.UserCreate;
import org.esfr.BazarBEG.modelos.dtos.usuarios.UserUpdate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;

public interface IUsuarioService {

//    Page<Usuario> obtenerTodosPaginados(Pageable pageable);
//
//    Page<Usuario> buscarPorTermino(String termino, Pageable pageable);
//
//    Page<Usuario> buscarTodosPaginados(String nombre, String rolNombre, Pageable pageable);
//
//    List<Usuario> obtenerTodos();
//
//    Optional<Usuario> buscarPorId(Integer id);
//
//    Usuario crearOEditar(Usuario usuario);
//
//    void eliminarPorId(Integer id);
//
//    Optional<Usuario> obtenerPorEmail(String email);


    List<User> obtenerUsuarios();
    User obtenerPorEmail(String email);
    Page<User> obtenerTodosPaginados(Pageable pageable);
    LoginResponse loginObtener(String email, String password);
    User crear(UserCreate user);
    User editar(UserUpdate userUpdate);
}