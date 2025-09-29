package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product; // Importamos el DTO de Producto
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto; // Importamos el DTO de Categoría
import org.esfr.BazarBEG.modelos.dtos.productos.ProductCreation;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/productos")
public class ProductoController {


    @Autowired
    private IProductoService productoService;
    @Autowired
    private ICategoriaService categoriaService;

    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        @RequestParam("q") Optional<String> query) {
        List<Product> todosLosProductos = productoService.obtenerTodos();
        for (Product producto : todosLosProductos){
            producto.setImagen(productoService.obtenerImagen(producto.getId()));
        }

        String searchQuery = query.orElse("").toLowerCase().trim();

        List<Product> productosFiltrados;
        if (searchQuery.isBlank()) {
            productosFiltrados = todosLosProductos;
        } else {
            productosFiltrados = todosLosProductos.stream()
                    .filter(product -> product.getNombre().toLowerCase().contains(searchQuery)
                            || product.getCategoriaId() == Integer.parseInt(searchQuery))
                    .collect(Collectors.toList());
        }

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), productosFiltrados.size());

        List<Product> pageContent = List.of();
        if (start < productosFiltrados.size()) {
            pageContent = productosFiltrados.subList(start, end);
        }

        Page<Product> productos = new PageImpl<>(pageContent, pageable, productosFiltrados.size());

        // 4. The rest of your model logic can stay the same
        model.addAttribute("productos", productos);
        model.addAttribute("query", searchQuery);

        int totalPages = productos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "producto/index";
    }

    @GetMapping( value = "/imagen/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getImagenAdmin(@PathVariable("id") Integer id, Model model) {
        byte[] img = productoService.obtenerImagen(id);

        if (img != null && img.length > 0) {
            return ResponseEntity.ok().body(img);
        }

        return ResponseEntity.notFound().build();
    }

    // -------------------- CREAR (Usa Entidad/ID de Categoría) --------------------

    @GetMapping("/create")
    public String create(Model model) {
        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("producto", new ProductCreation());
        model.addAttribute("categorias", categorias);
        return "producto/create";
    }

    @PostMapping("/save")
    public String save(
            ProductCreation producto,
            @RequestParam("fileImagen") MultipartFile fileImagen,
            BindingResult result,
            Model model,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            model.addAttribute(producto);
            // Si hay error, volvemos a cargar las categorías
            model.addAttribute("categorias", categoriaService.obtenerTodos());
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "producto/create";
        }


        try {
            if (producto.getId() == null) {
                if(producto.getImagen() != null){
                    productoService.crear(producto);
                }
            }
            else if (producto.getId() != null && producto.getId() > 0) {
                if(producto.getImagen() != null){
                    productoService.editar(producto);
                }
                else {
                    producto.setImagen(productoService.obtenerImagen(producto.getId()));
                    productoService.editar(producto);
                }
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
            return "redirect:/productos/create";
        }

        attributes.addFlashAttribute("msg", "Producto guardado correctamente");
        return "redirect:/productos";
    }



    // -------------------- DETALLES (Usa DTO) --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Product producto = productoService.buscarPorId(id);

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        return "producto/details";
    }

    // -------------------- EDITAR (Usa Entidad para formulario) --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Product producto = productoService.buscarPorId(id);
        ProductCreation creation = new ProductCreation();
        creation.setId(producto.getId());
        creation.setNombre(producto.getNombre());
        creation.setDescripcion(producto.getDescripcion());
        creation.setPrecio(producto.getPrecio());
        creation.setStatus(producto.getStatus());
        creation.setStock(producto.getStock());
        creation.setCategoriaId(producto.getCategoriaId());
        creation.setImagen(producto.getImagen());
        model.addAttribute("producto", creation);

        model.addAttribute("categorias", categoriaService.obtenerTodos());
        return "producto/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model) {
        Product producto = productoService.buscarPorId(id);
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        model.addAttribute("producto", producto);
        return "producto/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes) {

        try{
            productoService.eliminarPorId(id);
            attributes.addFlashAttribute("msg", "Producto eliminado correctamente");
        } catch (Exception e){
            attributes.addFlashAttribute("error", "El producto no existe");
        }

        return "redirect:/productos";
    }


}
