package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Carrito;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    @Autowired
    private Carrito carrito;

    @Autowired
    private IProductoService productoService;

//    @GetMapping
//    public String verCarrito(Model model) {
//        List<Producto> productosEnCarrito = new ArrayList<>();
//        double total = 0.0;
//
//        for (Map.Entry<Long, Integer> entry : carrito.getItems().entrySet()) {
//            Producto producto = productoService.obtenerProductoActivoPorId(entry.getKey()).orElse(null);
//            if (producto != null) {
//                productosEnCarrito.add(producto);
//                total += producto.getPrecio() * entry.getValue();
//            }
//        }
//
//        model.addAttribute("productos", productosEnCarrito);
//        model.addAttribute("total", total);
//        model.addAttribute("items", carrito.getItems());
//        return "carrito/carrito";
//    }

//    @PostMapping("/agregar/{id}")
//    public String agregarProducto(@PathVariable("id") Long id, @RequestParam(defaultValue = "1") int cantidad, RedirectAttributes redirectAttributes) {
//        Optional<Producto> productoOptional = productoService.obtenerProductoActivoPorId(id);
//        if (productoOptional.isPresent()) {
//            carrito.agregarProducto(id, cantidad);
//            redirectAttributes.addFlashAttribute("agregadoExitosamente", true);
//        }
//        return "redirect:/catalogo/producto/" + id;
//    }
//
//    @PostMapping("/eliminar/{id}")
//    public String eliminarProducto(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
//        carrito.eliminarProducto(id);
//        redirectAttributes.addFlashAttribute("eliminadoExitosamente", true);
//        return "redirect:/carrito";
//    }
//
//    @PostMapping("/actualizar/{id}")
//    public String actualizarCantidad(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad, RedirectAttributes redirectAttributes) {
//        carrito.actualizarCantidad(id, cantidad);
//        redirectAttributes.addFlashAttribute("actualizadoExitosamente", true);
//        return "redirect:/carrito";
//    }
}