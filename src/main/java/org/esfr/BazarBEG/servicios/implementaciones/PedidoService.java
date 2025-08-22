package org.esfr.BazarBEG.servicios.implementaciones;

import java.util.List;
import java.util.Optional;

import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.repositorios.IPedidoRepository;
import org.esfr.BazarBEG.servicios.interfaces.IPedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PedidoService implements IPedidoService {
    
     @Autowired
    private IPedidoRepository pedidoRepository;

    @Override
    public Page<Pedido> buscarTodosPaginados(Pageable pageable) {
        return pedidoRepository.findAll(pageable);
    }

    @Override
    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> buscarPorId(Integer id) {
        return pedidoRepository.findByIdWithDetailsAndUser(id);
    }

    @Override
    public Pedido crearOEditar(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    @Override
    public void eliminarPorId(Integer id) {
        pedidoRepository.deleteById(id);
    }
}
