package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Pedido;
import org.esfr.BazarBEG.modelos.DetallePedido;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.servicios.interfaces.IPedidoService;
import org.esfr.BazarBEG.servicios.interfaces.IDetallePedidoService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private IPedidoService pedidoService;

    @Autowired
    private IDetallePedidoService detallePedidoService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IUsuarioService usuarioService;

    // ----------------- Listado de Pedidos -----------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Integer page,
                        @RequestParam("size") Integer size) {

        int currentPage = (page == null || page < 1) ? 0 : page - 1;
        int pageSize = (size == null || size < 1) ? 5 : size;

        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Pedido> pedidos = pedidoService.buscarTodosPaginados(pageable);

        model.addAttribute("pedidos", pedidos);

        int totalPages = pedidos.getTotalPages();
        if (totalPages > 0) {
            model.addAttribute("pageNumbers", java.util.stream.IntStream.rangeClosed(1, totalPages).boxed().toList());
        }

        return "pedido/index";
    }

    // ----------------- Crear Pedido -----------------
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        return "pedido/create";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Pedido pedido,
                       BindingResult result,
                       @RequestParam("productoIds") List<Integer> productoIds,
                       @RequestParam("cantidades") List<Integer> cantidades,
                       RedirectAttributes attributes,
                       Model model) {

        if (result.hasErrors() || productoIds.isEmpty() || cantidades.isEmpty()) {
            model.addAttribute("usuarios", usuarioService.obtenerTodos());
            model.addAttribute("productos", productoService.obtenerTodos());
            attributes.addFlashAttribute("error", "Debe seleccionar al menos un producto y su cantidad");
            return "pedido/create";
        }

        float total = 0;
        for (int i = 0; i < productoIds.size(); i++) {
            Producto p = productoService.buscarPorId(productoIds.get(i)).orElse(null);
            if (p != null) {
                DetallePedido detalle = new DetallePedido();
                detalle.setProducto(p);
                detalle.setCantidad(cantidades.get(i));
                detalle.setPrecioUnitario(p.getPrecio());
                detalle.setPedido(pedido);
                total += p.getPrecio() * cantidades.get(i);
                pedido.getDetalles().add(detalle);
            }
        }

        pedido.setTotal(total);
        pedido.setFechaPedido(new Date());
        pedidoService.crearOEditar(pedido);
        attributes.addFlashAttribute("msg", "Pedido creado correctamente");
        return "redirect:/pedidos";
    }

    // ----------------- Ver detalles -----------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id).orElse(null);
        if (pedido == null) {
            model.addAttribute("error", "Pedido no encontrado");
            return "redirect:/pedidos";
        }
        model.addAttribute("pedido", pedido);
        return "pedido/details";
    }

    // ----------------- Editar Pedido -----------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Pedido pedido = pedidoService.buscarPorId(id).orElse(null);
        if (pedido == null) {
            model.addAttribute("error", "Pedido no encontrado");
            return "redirect:/pedidos";
        }
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuarios", usuarioService.obtenerTodos());
        model.addAttribute("productos", productoService.obtenerTodos());
        return "pedido/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer id,
                         @ModelAttribute Pedido pedido,
                         RedirectAttributes attributes) {
        pedido.setId(id);
        pedidoService.crearOEditar(pedido);
        attributes.addFlashAttribute("msg", "Pedido actualizado correctamente");
        return "redirect:/pedidos";
    }

    // ----------------- Eliminar Pedido -----------------
    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        pedidoService.eliminarPorId(id);
        attributes.addFlashAttribute("msg", "Pedido eliminado correctamente");
        return "redirect:/pedidos";
    }
}
