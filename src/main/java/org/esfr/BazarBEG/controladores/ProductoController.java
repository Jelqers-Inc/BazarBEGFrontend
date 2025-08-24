package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.esfr.BazarBEG.servicios.interfaces.IProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
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

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/productos/";

    @Autowired
    private IProductoService productoService;
    @Autowired
    private ICategoriaService categoriaService;

    // -------------------- LISTADO --------------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        @RequestParam("q") Optional<String> query) { // Añade el parámetro de búsqueda
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(20);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Producto> productos;
        if (query.isPresent() && !query.get().isBlank()) {
            productos = productoService.buscarPorFiltroPaginado(query.get(), null, pageable);
            model.addAttribute("query", query.get()); // Pasa el término a la vista
        } else {
            // Si no hay búsqueda, muestra todos los productos paginados
            productos = productoService.buscarTodosPaginados(pageable);
        }

        model.addAttribute("productos", productos);

        int totalPages = productos.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "producto/index";
    }

    // -------------------- CREAR --------------------

    @GetMapping("/create")
    public String create(Producto producto, Model model) {
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        return "producto/create";
    }

    @PostMapping("/save")
    public String save(
            Producto producto,
            @RequestParam("categoria.id") Integer categoriaId,
            @RequestParam("fileImagen") MultipartFile fileImagen,
            BindingResult result,
            Model model,
            RedirectAttributes attributes) {

        if (result.hasErrors()) {
            model.addAttribute(producto);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "producto/create";
        }

        producto.setCategoria(categoriaService.buscarPorId(categoriaId).orElse(null));

        try {
            if (fileImagen != null && !fileImagen.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = fileImagen.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(fileImagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // **CAMBIO CRUCIAL:** Guarda la ruta completa que el navegador pueda interpretar
                producto.setImagen("/uploads/productos/" + fileName);

            } else if (producto.getId() != null && producto.getId() > 0) {
                Producto productoExistente = productoService.buscarPorId(producto.getId()).orElse(null);
                if (productoExistente != null) {
                    producto.setImagen(productoExistente.getImagen());
                }
            }
        } catch (IOException e) {
            attributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
            return "redirect:/productos/create";
        }

        productoService.crearOEditar(producto);
        attributes.addFlashAttribute("msg", "Producto guardado correctamente");
        return "redirect:/productos";
    }



    // -------------------- DETALLES --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id).orElse(null);
        model.addAttribute("producto", producto);
        return "producto/details";
    }

    // -------------------- EDITAR --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id).orElse(null);
        model.addAttribute("producto", producto);

        // Pasar también la lista de categorías
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        return "producto/edit";
    }

    // -------------------- ELIMINAR --------------------
    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model) {
        Producto producto = productoService.buscarPorId(id).orElse(null);
        model.addAttribute("producto", producto);
        return "producto/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes) {
        Optional<Producto> prodData = productoService.buscarPorId(id);

        if (prodData.isPresent()) {
            Producto producto = prodData.get();

            if (producto.getImagen() != null) {
                try {
                    // Extrae solo el nombre del archivo de la ruta almacenada
                    String fileName = Paths.get(producto.getImagen()).getFileName().toString();
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    Path filePath = uploadPath.resolve(fileName);
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    attributes.addFlashAttribute("error", "Error al eliminar la imagen: " + e.getMessage());
                    return "redirect:/productos";
                }
            }

            productoService.eliminarPorId(id);
            attributes.addFlashAttribute("msg", "Producto eliminado correctamente");
        } else {
            attributes.addFlashAttribute("error", "El producto no existe");
        }

        return "redirect:/productos";
    }
    @GetMapping("/imagen/{id}")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable("id") Integer id) {
        Optional<Producto> productoOpt = productoService.buscarPorId(id);

        if (productoOpt.isPresent() && productoOpt.get().getImagen() != null) {
            try {
                String rutaImagen = productoOpt.get().getImagen();
                String nombreArchivo = Paths.get(rutaImagen).getFileName().toString();
                Path filePath = Paths.get(UPLOAD_DIR).resolve(nombreArchivo).normalize();

                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists() || resource.isReadable()) {
                    String contentType = Files.probeContentType(filePath);
                    if (contentType == null) {
                        contentType = "application/octet-stream";
                    }
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(contentType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                }
            } catch (IOException e) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
