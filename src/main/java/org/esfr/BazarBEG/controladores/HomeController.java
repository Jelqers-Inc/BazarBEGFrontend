package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.servicios.implementaciones.ProductoService;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/") //este controlador maneja la raíz
public class HomeController {

    @Autowired
    private ICategoriaService categoriaService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public String index(Model model){
        // Obtener todas las categorías de la base de datos
        List<Categoria> categorias = categoriaService.obtenerTodos();

        // Agregar la lista de categorías al modelo
        model.addAttribute("categorias", categorias);

        // Obtener solo los productos activos a través del servicio
        List<Producto> productos = productoService.obtenerProductosActivos();

        // Agregar los productos filtrados al modelo
        model.addAttribute("productos", productos);

        // Devolver la vista (el archivo home/index.html)
        return "home/index";
    }
}