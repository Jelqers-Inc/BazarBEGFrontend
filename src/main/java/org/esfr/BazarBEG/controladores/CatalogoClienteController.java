package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/catalogo")
public class CatalogoClienteController {

    @Autowired
    private IProductoService productoService;

    @Autowired
    private ICategoriaService categoriaService;

    @GetMapping
    public String mostrarCatalogo(Model model,
                                  @RequestParam(value = "q", required = false) String q) {

        List<Product> productos;

        if (q != null && !q.isEmpty()) {
            productos = productoService.obtenerTodos().stream()
                    .filter(x -> x.getNombre().equalsIgnoreCase(q)).collect(Collectors.toList());
        } else {
            productos = productoService.obtenerTodos();
        }

        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("query", q);
        model.addAttribute("searchAction", "/catalogo");

        return "catalogo/catalogoC";
    }



    @GetMapping("/categoria/{id}")
    public String mostrarProductosPorCategoria(@PathVariable("id") Integer id, Model model) {
        List<Product> products = productoService.obtenerTodos().stream()
                .filter(x -> x.getCategoriaId().equals(id)).collect(Collectors.toList());

        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("productos", products);
        model.addAttribute("categorias", categorias);
        return "catalogo/catalogoC";
    }

    @GetMapping("/producto/{id}")
    public String mostrarDetallesProducto(@PathVariable("id") Integer id, Model model) {
        Product producto = productoService.buscarPorId(id);

        if (producto.getId() != null) {
            List<Categoria> categorias = categoriaService.obtenerTodos();
            model.addAttribute("producto", producto);
            model.addAttribute("categorias", categorias);
            return "catalogo/detallesProducto";
        } else {
            return "redirect:/catalogo";
        }
    }


}