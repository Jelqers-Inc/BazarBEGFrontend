package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.dtos.carrito.CarritoItemDTO;
import org.esfr.BazarBEG.repositorios.CarritoReopsitory;
import org.esfr.BazarBEG.servicios.interfaces.ICarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoService implements ICarritoService {

    @Autowired
    CarritoReopsitory carritoReopsitory;

    @Override
    public List<CarritoItemDTO> obtenerTodo() {
        return carritoReopsitory.obtenerTodo();
    }

    @Override
    public String crear(Integer idProducto, Integer cantidad) {
        return carritoReopsitory.crear(idProducto, cantidad);
    }

    @Override
    public void eliminar(Integer id) {
        carritoReopsitory.eliminar(id);
    }
}
