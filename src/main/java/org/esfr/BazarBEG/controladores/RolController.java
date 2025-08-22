package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.servicios.interfaces.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/roles")
public class RolController {

    @Autowired
    private IRolService rolService;

    // -------------------- LISTADO DE ROLES --------------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        Page<Rol> roles = rolService.buscarTodosPaginados(pageable);
        model.addAttribute("roles", roles);

        int totalPages = roles.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "rol/index";
    }

    // -------------------- MOSTRAR FORMULARIO DE CREACIÓN --------------------
    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("rol", new Rol());
        return "rol/create";
    }

    // -------------------- GUARDAR UN NUEVO ROL --------------------
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Rol rol, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "rol/create";
        }
        rolService.crearOEditar(rol);
        redirect.addFlashAttribute("msg", "Rol guardado exitosamente");
        return "redirect:/roles";
    }

    // -------------------- DETALLES DE UN ROL --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Rol rol = rolService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        model.addAttribute("rol", rol);
        return "rol/details";
    }

    // -------------------- MOSTRAR FORMULARIO DE EDICIÓN --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Rol rol = rolService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        model.addAttribute("rol", rol);
        return "rol/edit";
    }

    // -------------------- ACTUALIZAR UN ROL --------------------
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer id, @Valid @ModelAttribute Rol rol, BindingResult result, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return "rol/edit";
        }
        rol.setId(id);
        rolService.crearOEditar(rol);
        redirect.addFlashAttribute("msg", "Rol actualizado exitosamente");
        return "redirect:/roles";
    }

    // -------------------- ELIMINAR UN ROL --------------------
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        rolService.eliminarPorId(id);
        redirect.addFlashAttribute("msg", "Rol eliminado exitosamente");
        return "redirect:/roles";
    }

    // -------------------- VISTA DE CONFIRMACIÓN DE ELIMINACIÓN --------------------
    @GetMapping("/delete-confirm/{id}")
    public String showDeleteConfirmation(@PathVariable("id") Integer id, Model model) {
        Rol rol = rolService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        model.addAttribute("rol", rol);
        return "rol/delete";
    }
}