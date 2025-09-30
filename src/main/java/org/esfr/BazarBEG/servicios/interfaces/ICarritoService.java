package org.esfr.BazarBEG.servicios.interfaces;

import org.esfr.BazarBEG.modelos.dtos.carrito.CarritoItemDTO;

import java.util.List;

public interface ICarritoService {
    List<CarritoItemDTO> obtenerTodo();
    String crear(Integer idProducto, Integer cantidad);
    void eliminar(Integer id);
}
