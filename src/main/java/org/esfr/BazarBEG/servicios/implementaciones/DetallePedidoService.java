package org.esfr.BazarBEG.servicios.implementaciones;

import org.esfr.BazarBEG.modelos.DetallePedido;
import org.esfr.BazarBEG.repositorios.IDetallePedidoRepository;
import org.esfr.BazarBEG.servicios.interfaces.IDetallePedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetallePedidoService implements IDetallePedidoService {
 
 @Autowired
    private IDetallePedidoRepository detallePedidoRepository;

    @Override
    public Page<DetallePedido> buscarTodosPaginados(Pageable pageable) {
        return detallePedidoRepository.findAll(pageable);
    }

    @Override
    public List<DetallePedido> obtenerTodos() {
        return detallePedidoRepository.findAll();
    }

    @Override
    public Optional<DetallePedido> buscarPorId(Integer id) {
        return detallePedidoRepository.findById(id);
    }

    @Override
    public DetallePedido crearOEditar(DetallePedido detallePedido) {
        return detallePedidoRepository.save(detallePedido);
    }

    @Override
    public void eliminarPorId(Integer id) {
        detallePedidoRepository.deleteById(id);
    }
    
}
