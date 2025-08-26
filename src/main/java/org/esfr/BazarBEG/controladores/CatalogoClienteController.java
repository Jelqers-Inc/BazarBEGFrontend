package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/catalogo")
public class CatalogoClienteController {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private ICategoriaService categoriaService;

    @GetMapping
    public String mostrarCatalogo(Model model) {
        List<Producto> productos = productoService.obtenerProductosActivos();
        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        return "catalogo/catalogoC";
    }

    @GetMapping("/categoria/{id}")
    public String mostrarProductosPorCategoria(@PathVariable("id") Long id, Model model) {
        List<Producto> productos = productoService.obtenerProductosPorCategoriaActivos(id);
        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        return "catalogo/catalogoC";
    }

    @GetMapping("/producto/{id}")
    public String mostrarDetallesProducto(@PathVariable("id") Long id, Model model) {
        Optional<Producto> producto = productoService.obtenerProductoActivoPorId(id);

        if (producto.isPresent()) {
            model.addAttribute("producto", producto.get());
            return "catalogo/detallesProducto";
        } else {
            return "redirect:/catalogo";
        }
    }


}