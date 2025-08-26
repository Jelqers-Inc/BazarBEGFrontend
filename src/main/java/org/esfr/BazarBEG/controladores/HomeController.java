package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private IProductoService productoService; // Asegúrate de que este esté inyectado

    @GetMapping
    public String index(Model model) {
        // Obtener todas las categorías para la sección de "Comprar por Categorías"
        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("categorias", categorias);

        // Obtener una lista de productos destacados para mostrar en la página de inicio
        List<Producto> productosDestacados = productoService.obtenerProductosActivos();
        model.addAttribute("productosDestacados", productosDestacados);

        return "home/index";
    }
}