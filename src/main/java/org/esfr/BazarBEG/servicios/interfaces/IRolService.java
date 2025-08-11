package org.esfr.BazarBEG.servicios.interfaces;

import org.esfr.BazarBEG.modelos.Rol;

import java.util.List;

public interface IRolService {
    List<Rol> obtenerTodos();

    Rol obtenerPorId(Integer rolId);
}
