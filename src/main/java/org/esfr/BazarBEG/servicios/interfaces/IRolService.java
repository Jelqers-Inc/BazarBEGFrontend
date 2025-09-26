package org.esfr.BazarBEG.servicios.interfaces;

import java.util.List;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.modelos.dtos.roles.Role;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface IRolService{
// Estos métodos están comentados porque probablemente no se usan directamente con el DTO
//Page<Rol> buscarTodosPaginados(Pageable pageable);
//
//    List<Rol> obtenerTodos();
//
//    Optional<Rol> buscarPorId(Integer id);
//
//    Rol crearOEditar(Rol rol);
//
//    void eliminarPorId(Integer id);
//
//    Page<Rol> obtenerTodosPaginados(Pageable pageable);
//
//    Page<Rol> buscarPorTermino(String termino, Pageable pageable);


    // Peticiones usando el DTO 'Role', similar a como lo haces en IUsuarioService
    List<Role> obtenerTodos();
    Role obtenerPorId(Integer id);
    Page<Role> obtenerTodosPaginados(Pageable pageable);
    Role crear(Role role);
    Role editar(Role role);
    void eliminar(Integer id);
}