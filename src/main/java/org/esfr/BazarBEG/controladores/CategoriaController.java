package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Categoria;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
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
@RequestMapping("/categorias")
public class CategoriaController {

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/categorias/";

    @Autowired
    private ICategoriaService categoriaService;

    // -------------------- LISTADO --------------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        @RequestParam("q") Optional<String> query) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Categoria> categorias;
        if (query.isPresent() && !query.get().isBlank()) {
            // Si el parámetro de búsqueda 'q' existe, busca por nombre
            categorias = categoriaService.buscarPorNombrePaginado(query.get(), pageable);
            model.addAttribute("query", query.get()); // Pasa el término de búsqueda a la vista
        } else {
            // Si no hay parámetro de búsqueda, muestra todas las categorías
            categorias = categoriaService.buscarTodosPaginados(pageable);
        }

        model.addAttribute("categorias", categorias);

        int totalPages = categorias.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "categoria/index";
    }

    // -------------------- CREAR --------------------
    @GetMapping("/create")
    public String create(Categoria categoria) {
        return "categoria/create";
    }

    @PostMapping("/save")
    public String save(Categoria categoria,@RequestParam("fileImagen") MultipartFile fileImagen, BindingResult result,
                       Model model, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            model.addAttribute(categoria);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error.");
            return "categoria/create";
        }

        try {
            if (fileImagen != null && !fileImagen.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = fileImagen.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(fileImagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // Si es edición, elimina la imagen anterior
                if (categoria.getId() != null && categoria.getId() > 0) {
                    Categoria categoriaExistente = categoriaService.buscarPorId(categoria.getId()).orElse(null);
                    if (categoriaExistente != null && categoriaExistente.getImagen() != null) {
                        Path fileAnterior = uploadPath.resolve(categoriaExistente.getImagen());
                        Files.deleteIfExists(fileAnterior);
                    }
                }

                categoria.setImagen(fileName); // guarda el nombre del archivo
            } else if (categoria.getId() != null && categoria.getId() > 0) {
                // Mantener la imagen anterior si no suben nueva
                Categoria categoriaExistente = categoriaService.buscarPorId(categoria.getId()).orElse(null);
                if (categoriaExistente != null) {
                    categoria.setImagen(categoriaExistente.getImagen());
                }
            }

        } catch (IOException e) {
            attributes.addFlashAttribute("error", "Error al procesar la imagen: " + e.getMessage());
            return "redirect:/categorias/create";
        }

        categoriaService.crearOEditar(categoria);
        attributes.addFlashAttribute("msg", "Categoría guardada correctamente");
        return "redirect:/categorias";
    }

    // -------------------- DETALLES --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id).orElse(null);
        model.addAttribute("categoria", categoria);
        return "categoria/details";
    }

    // -------------------- EDITAR --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id).orElse(null);
        model.addAttribute("categoria", categoria);
        return "categoria/edit";
    }

    // -------------------- ELIMINAR --------------------
    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model) {
        Categoria categoria = categoriaService.buscarPorId(id).orElse(null);
        model.addAttribute("categoria", categoria);
        return "categoria/delete";
    }

    @PostMapping("/delete")
public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes) {
    Optional<Categoria> catData = categoriaService.buscarPorId(id);

    if (catData.isPresent()) {
        try {
            Categoria categoria = catData.get();
            categoriaService.eliminarPorId(id);

            // Solo si la eliminación de la BD es exitosa, eliminamos el archivo.
            if (categoria.getImagen() != null) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                Path filePath = uploadPath.resolve(categoria.getImagen());
                Files.deleteIfExists(filePath);
            }

            attributes.addFlashAttribute("msg", "Categoría eliminada correctamente");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            attributes.addFlashAttribute("error", "No se puede eliminar la categoría porque contiene productos.");
            // Si hay un error, no hacemos nada con el archivo, la imagen se mantiene.
        } catch (IOException e) {
            attributes.addFlashAttribute("error", "Error al eliminar la imagen: " + e.getMessage());
        }
    } else {
        attributes.addFlashAttribute("error", "La categoría no existe.");
    }

    return "redirect:/categorias";
}

    // -------------------- SERVIR IMÁGENES --------------------
    @GetMapping("/imagen/{id}")
    @ResponseBody
    public ResponseEntity<Resource> mostrarImagen(@PathVariable Integer id) {
        try {
            Categoria categoria = categoriaService.buscarPorId(id).orElse(null);
            if (categoria != null && categoria.getImagen() != null) {
                Path filePath = Paths.get(UPLOAD_DIR, categoria.getImagen());
                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() || resource.isReadable()) {
                    String mimeType = Files.probeContentType(filePath);
                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(mimeType))
                            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                            .body(resource);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

}
