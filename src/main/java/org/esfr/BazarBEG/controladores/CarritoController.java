package org.esfr.BazarBEG.controladores;


import org.esfr.BazarBEG.modelos.dtos.carrito.CarritoItemDTO;
import org.esfr.BazarBEG.modelos.dtos.productos.Product;
import org.esfr.BazarBEG.servicios.interfaces.ICarritoService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {


    @Autowired
    private IProductoService productoService;

    @Autowired
    private ICarritoService carritoService;

    @GetMapping
    public String verCarrito(Model model) {

        List<CarritoItemDTO> carritoItems = carritoService.obtenerTodo();

        double total = 0.0;
        if (carritoItems != null) {
            total = carritoItems.stream()
                    .mapToDouble(item -> item.getPrecio().doubleValue() * item.getCantidad())
                    .sum();
        }

        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("total", total);
        return "carrito/carrito";
    }

    @PostMapping("/agregar/{id}")
    public String agregarProducto(@PathVariable("id") Integer id, @RequestParam(defaultValue = "1") int cantidad, RedirectAttributes redirectAttributes) {
        Product producto = productoService.buscarPorId(id);
        if (producto.getId() != null) {
            carritoService.crear(id, cantidad);
            redirectAttributes.addFlashAttribute("agregadoExitosamente", true);
        }
        return "redirect:/catalogo/producto/" + id;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarDeCarrito(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes){
        if(id != null){
            carritoService.eliminar(id);
            redirectAttributes.addFlashAttribute("eliminadoExitosamente", true);
        }

        return "redirect:/carrito";
    }

}