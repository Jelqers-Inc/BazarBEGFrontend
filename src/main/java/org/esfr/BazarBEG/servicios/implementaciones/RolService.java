package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.modelos.dtos.roles.Role;
import org.esfr.BazarBEG.repositorios.IRolRepository;
import org.esfr.BazarBEG.repositorios.RolRepository;
import org.esfr.BazarBEG.servicios.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RolService implements IRolService{
    
    @Autowired
    private RolRepository rolRepository;

//     @Override
//    public Page<Rol> buscarTodosPaginados(Pageable pageable) {
//        return rolRepository.findAll(pageable);
//    }
//
//    @Override
//    public List<Rol> obtenerTodos() {
//        return rolRepository.findAll();
//    }
//
//    @Override
//    public Optional<Rol> buscarPorId(Integer id) {
//        return rolRepository.findById(id);
//    }
//
//    @Override
//    public Rol crearOEditar(Rol rol) {
//        return rolRepository.save(rol);
//    }
//
//    @Override
//    public void eliminarPorId(Integer id) {
//        rolRepository.deleteById(id);
//    }
//
//    @Override
//    public Page<Rol> obtenerTodosPaginados(Pageable pageable) {
//        return rolRepository.findAll(pageable);
//    }
//
//    @Override
//    public Page<Rol> buscarPorTermino(String termino, Pageable pageable) {
//        return rolRepository.findByNombreContainingIgnoreCase(termino, pageable);
//    }

    @Override
    public List<Role> obtenerTodos(){
        return rolRepository.obtenerTodosRoles();
    }
}
