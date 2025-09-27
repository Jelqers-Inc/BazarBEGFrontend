package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;
import org.esfr.BazarBEG.servicios.interfaces.ICategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

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

        Page<Categoriadto> categorias;
        String searchQuery = query.orElse("").trim();

        if (!searchQuery.isBlank()) {
            categorias = categoriaService.buscarPorTermino(searchQuery, pageable);
        } else {
            categorias = categoriaService.obtenerTodosPaginados(pageable);
        }

        model.addAttribute("categorias", categorias);
        model.addAttribute("query", searchQuery);

        int totalPages = categorias.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // La vista mostrará el mensaje de error o éxito que venga de RedirectAttributes

        return "categoria/index";
    }

    // -------------------- CREAR y GUARDAR --------------------
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("categoria", new Categoriadto());
        return "categoria/create";
    }

    @PostMapping("/save")
    public String save(@Valid Categoriadto categoria,
                       @RequestParam("fileImagen") MultipartFile fileImagen,
                       BindingResult result,
                       Model model, RedirectAttributes attributes) {

        if (result.hasErrors()) {
            model.addAttribute(categoria);
            attributes.addFlashAttribute("error", "No se pudo guardar debido a un error de validación.");
            return "categoria/create";
        }

        // Aquí se asume que la API maneja la URL/nombre de la imagen
        if (fileImagen != null && !fileImagen.isEmpty()) {
            // Lógica para establecer el nombre de la imagen en el DTO si es necesario
        }

        try {
            if (categoria.getId() == null) {
                // Es una creación
                categoriaService.crear(categoria);
            } else {
                // Es una edición
                categoriaService.editar(categoria);
            }
            attributes.addFlashAttribute("msg", "Categoría guardada correctamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "Error al guardar la categoría. Revise la conexión con la API y el JWT.");
        }

        return "redirect:/categorias";
    }

    // -------------------- DETALLES, EDITAR, ELIMINAR (GET) --------------------

    @GetMapping("/details/{id}")
    public String details(@PathVariable Integer id, Model model, RedirectAttributes attributes) {
        Categoriadto categoria = categoriaService.obtenerPorId(id);

        if (categoria == null) {
            // Redirección si la API falla o no encuentra el ID
            attributes.addFlashAttribute("error", "Error: La categoría ID " + id + " no existe o el servicio de API no está disponible.");
            return "redirect:/categorias";
        }

        model.addAttribute("categoria", categoria);
        return "categoria/details";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {

        Categoriadto categoria = categoriaService.obtenerPorId(id);

        if (categoria == null) {
            // Redirección si la API falla o no encuentra el ID
            attributes.addFlashAttribute("error", "Error: La categoría ID " + id + " no se pudo cargar para edición. El servicio de API no está disponible.");
            return "redirect:/categorias";
        }

        model.addAttribute("categoria", categoria);
        return "categoria/edit";
    }

    @GetMapping("/remove/{id}")
    public String remove(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
        Categoriadto categoria = categoriaService.obtenerPorId(id);

        if (categoria == null) {
            // Redirección si la API falla o no encuentra el ID
            attributes.addFlashAttribute("error", "Error: La categoría ID " + id + " no se pudo cargar para eliminación. El servicio de API no está disponible.");
            return "redirect:/categorias";
        }

        model.addAttribute("categoria", categoria);
        return "categoria/delete";
    }

    // -------------------- ELIMINAR (POST) --------------------
    @PostMapping("/delete")
    public String delete(@RequestParam("id") Integer id, RedirectAttributes attributes) {
        try {
            categoriaService.eliminar(id);
            attributes.addFlashAttribute("msg", "Categoría eliminada correctamente");
        } catch (Exception e) {
            attributes.addFlashAttribute("error", "No se pudo eliminar la categoría. Revise si contiene productos.");
        }

        return "redirect:/categorias";
    }
}