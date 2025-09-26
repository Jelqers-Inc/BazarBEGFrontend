package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.dtos.roles.Role;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.repositorios.RolRepository;
import org.esfr.BazarBEG.servicios.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService implements IRolService {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public List<Role> obtenerTodos() {
        return rolRepository.obtenerTodosRoles();
    }

    @Override
    public Role obtenerPorId(Integer id) {
        return rolRepository.obtenerPorId(id);
    }

    @Override
    public Page<Role> obtenerTodosPaginados(Pageable pageable) {
        // 1. Obtiene la lista completa de roles de la API
        List<Role> roles = obtenerTodos();

        // 2. Calcula los índices para la paginación
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        // 3. Crea una sublista con los elementos de la página actual
        List<Role> list;
        if (roles.size() < startItem) {
            list = List.of();
        } else {
            int toIndex = Math.min(startItem + pageSize, roles.size());
            list = roles.subList(startItem, toIndex);
        }

        // 4. Crea y retorna un objeto Page con la sublista
        Page<Role> rolPage = new PageImpl<>(list, pageable, roles.size());

        return rolPage;
    }

    @Override
    public Role crear(Role role) {
        return rolRepository.crear(role);
    }

    @Override
    public Role editar(Role role) {
        return rolRepository.actualizar(role);
    }

    @Override
    public void eliminar(Integer id) {
        rolRepository.eliminar(id);
    }
}