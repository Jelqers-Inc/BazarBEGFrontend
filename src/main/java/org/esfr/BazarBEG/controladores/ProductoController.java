package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.modelos.Producto;
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product; // Importamos el DTO de Producto
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto; // Importamos el DTO de Categoría
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

    // -------------------- LISTADO (Usa DTO) --------------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        @RequestParam("q") Optional<String> query) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(20);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        // **CAMBIO:** Ahora usamos Page<Product> DTOs
        Page<Product> productos;
        if (query.isPresent() && !query.get().isBlank()) {
            // Asumiendo que buscarPorFiltroPaginado ahora retorna Page<Product>
            productos = productoService.buscarPorFiltroPaginado(query.get(), null, pageable);
            model.addAttribute("query", query.get());
        } else {
            // Asumiendo que buscarTodosPaginados ahora retorna Page<Product>
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

    // -------------------- CREAR (Usa Entidad/ID de Categoría) --------------------

    @GetMapping("/create")
    public String create(Producto producto, Model model) {
        // Obtenemos List<Category> DTO para el formulario (select options)
        List<Categoria> categorias = categoriaService.obtenerTodos();
        model.addAttribute("categorias", categorias);
        return "producto/create";
    }

    @PostMapping("/save")
    public String save(
            Producto producto, // Usamos la Entidad para manejar la data del formulario
            @RequestParam("categoria.id") Integer categoriaId, // Recibimos el ID de la categoría
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

        // Creamos un stub de Categoria (Entidad) con solo el ID para que JPA pueda guardar la relación.
        // No necesitamos cargar la categoría completa de la API aquí.
        Categoria categoriaStub = new Categoria();
        categoriaStub.setId(categoriaId);
        producto.setCategoria(categoriaStub);


        try {
            if (fileImagen != null && !fileImagen.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = fileImagen.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(fileImagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                producto.setImagen("/uploads/productos/" + fileName);

            } else if (producto.getId() != null && producto.getId() > 0) {
                // Para edición, si no se sube una imagen nueva, conservamos la existente.
                Optional<Producto> productoExistenteOpt = productoService.obtenerProductoActivoPorId((long) producto.getId());
                if (productoExistenteOpt.isPresent()) {
                    producto.setImagen(productoExistenteOpt.get().getImagen());
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



    // -------------------- DETALLES (Usa DTO) --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        // **CAMBIO:** Buscar por ID ahora devuelve un Optional<Product> DTO
        Product producto = productoService.buscarPorId(id).orElse(null);
        model.addAttribute("producto", producto);
        return "producto/details";
    }

    // -------------------- EDITAR (Usa Entidad para formulario) --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        // Necesitamos la entidad Producto para rellenar correctamente el formulario de edición
        // (especialmente para seleccionar el ID de la categoría existente).
        Producto producto = productoService.obtenerProductoActivoPorId((long) id).orElse(null);
        model.addAttribute("producto", producto);

        // Pasar también la lista de categorías (DTOs) para las opciones del select
        model.addAttribute("categorias", categoriaService.obtenerTodos());
        return "producto/edit";
    }

    // -------------------- ELIMINAR (Usa DTO para mostrar detalles, Entidad para eliminar) --------------------
    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model) {
        // **CAMBIO:** Usamos el DTO para mostrar detalles al usuario antes de confirmar
        Product producto = productoService.buscarPorId(id).orElse(null);
        model.addAttribute("producto", producto);
        return "producto/delete";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes) {
        // Usamos obtenerProductoActivoPorId para obtener la Entidad y la ruta de la imagen
        Optional<Producto> prodData = productoService.obtenerProductoActivoPorId((long) id);

        if (prodData.isPresent()) {
            Producto producto = prodData.get();

            if (producto.getImagen() != null) {
                try {
                    // Lógica para eliminar el archivo de imagen del disco
                    String fileName = Paths.get(producto.getImagen()).getFileName().toString();
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    Path filePath = uploadPath.resolve(fileName);
                    Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    attributes.addFlashAttribute("error", "Error al eliminar la imagen: " + e.getMessage());
                    // Continuamos con la eliminación de la entrada en la DB a pesar del error de archivo
                }
            }

            productoService.eliminarPorId(id);
            attributes.addFlashAttribute("msg", "Producto eliminado correctamente");
        } else {
            attributes.addFlashAttribute("error", "El producto no existe");
        }

        return "redirect:/productos";
    }

    // -------------------- IMAGEN --------------------
    @GetMapping("/imagen/{id}")
    public ResponseEntity<Resource> obtenerImagen(@PathVariable("id") Integer id) {
        // Usamos el método que devuelve la Entidad para obtener la ruta de la imagen
        Optional<Producto> productoOpt = productoService.obtenerProductoActivoPorId((long) id);

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
