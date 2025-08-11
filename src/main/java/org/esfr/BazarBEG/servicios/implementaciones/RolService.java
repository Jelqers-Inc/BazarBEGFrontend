package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.repositorios.IRolRepository;
import org.esfr.BazarBEG.servicios.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class RolService implements IRolService {
    @Autowired
    private IRolRepository rolRepository;

    @Override
    public List<Rol> obtenerTodos() {
        return rolRepository.findAll();
    }

    @Override
    public Rol obtenerPorId(Integer rolId) {
        return null;
    }
}
