package org.esfr.BazarBEG.servicios.implementaciones;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;
import org.esfr.BazarBEG.modelos.DetallePedido;
import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.Usuario;
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
        return pedidoRepository.findAllWithUser(pageable);
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

    @Override
    @Transactional
    public Pedido crearPedido(Usuario usuario, Map<Producto, Integer> productos, double total) {
        // 1️⃣ Validar que el usuario exista en la BD
        if (usuario.getId() == null) {
            throw new IllegalArgumentException("El usuario no tiene ID asignado (no persistido en BD)");
        }
        System.out.println("Usuario ID: " + usuario.getId());

        // 2️⃣ Validar que todos los productos tengan ID
        for (Producto producto : productos.keySet()) {
            if (producto.getId() == null) {
                throw new IllegalArgumentException(
                        "El producto '" + producto.getNombre() + "' no tiene ID asignado (no persistido en BD)"
                );
            }
            System.out.println("Producto: " + producto.getNombre() + ", ID: " + producto.getId());
        }

        // 3️⃣ Crear el pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaPedido(new Date());
        pedido.setTotal((float) total);
        pedido.setEstado("Pagado");

        // Guardar el pedido primero para generar ID
        pedido = pedidoRepository.saveAndFlush(pedido);
        System.out.println("Pedido creado con ID: " + pedido.getId());

        // 4️⃣ Crear los detalles del pedido y asociarlos
        for (Map.Entry<Producto, Integer> entry : productos.entrySet()) {
            Producto producto = entry.getKey();
            int cantidad = entry.getValue();

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);          // FK producto
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(producto.getPrecio());
            detalle.setPedido(pedido);              // FK pedido

            // Asociar el detalle al pedido (relación bidireccional)
            pedido.getDetalles().add(detalle);
        }

        // 5️⃣ Guardar nuevamente el pedido con los detalles en cascada
        pedido = pedidoRepository.save(pedido);
        System.out.println("Pedido y detalles guardados correctamente");

        return pedido;
    }



    @Override
    public List<Pedido> obtenerPedidosPorUsuario(Usuario usuario) {
        return pedidoRepository.findByUsuario(usuario);
    }

}
