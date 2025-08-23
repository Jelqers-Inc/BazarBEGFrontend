package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.servicios.interfaces.IRolService;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
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
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private IUsuarioService usuarioService;
    @Autowired
    private IRolService rolService;

    // -------------------- LISTADO CON PAGINACIÓN Y BÚSQUEDA --------------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        @RequestParam("nombre") Optional<String> nombre,
                        @RequestParam("rolNombre") Optional<String> rolNombre) {

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        String searchNombre = nombre.orElse("");
        String searchRolNombre = rolNombre.orElse("");

        Page<Usuario> usuarios = usuarioService.buscarTodosPaginados(searchNombre, searchRolNombre, pageable);
        model.addAttribute("usuarios", usuarios);

        int totalPages = usuarios.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Se añaden los parámetros de búsqueda al modelo para que persistan en la paginación
        model.addAttribute("searchNombre", searchNombre);
        model.addAttribute("searchRolNombre", searchRolNombre);

        return "usuario/index";
    }

    // -------------------- MOSTRAR FORMULARIO DE CREACIÓN --------------------
    @GetMapping("/create")
    public String create(Model model) {
        Usuario usuario = new Usuario();
        usuario.setRol(new Rol());
        usuario.setStatus(1);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/create";
    }


    // -------------------- GUARDAR NUEVO USUARIO --------------------
    @PostMapping("/save")
    public String save(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.obtenerTodos());
            return "usuario/create";
        }

        // Verificar rol seleccionado, si no asignar por defecto
        if (usuario.getRol() != null && usuario.getRol().getId() != 0) {
            Rol rolSeleccionado = rolService.buscarPorId(usuario.getRol().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + usuario.getRol().getId()));
            usuario.setRol(rolSeleccionado);
        } else {
            // Rol por defecto = CLIENTE (ID = 2)
            Rol rolPorDefecto = rolService.buscarPorId(2)
                    .orElseThrow(() -> new IllegalArgumentException("Rol por defecto no encontrado"));
            usuario.setRol(rolPorDefecto);
        }

        usuarioService.crearOEditar(usuario);
        redirect.addFlashAttribute("msg", "Usuario guardado exitosamente");
        return "redirect:/usuarios";
    }

    // -------------------- DETALLES DE UN USUARIO --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        model.addAttribute("usuario", usuario);
        return "usuario/details";
    }

    // -------------------- MOSTRAR FORMULARIO DE EDICIÓN --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/edit";
    }

    // -------------------- ACTUALIZAR USUARIO --------------------
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer id, @ModelAttribute Usuario usuario,
                         BindingResult result, RedirectAttributes redirect, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.obtenerTodos());
            return "usuario/edit";
        }

        Usuario usuarioExistente = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));

        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setEmail(usuario.getEmail());

        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
            Rol rolSeleccionado = rolService.buscarPorId(usuario.getRol().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + usuario.getRol().getId()));
            usuarioExistente.setRol(rolSeleccionado);
        }

        // Esta es la lógica clave: establecer el estado explícitamente a 1 o 0
        if (usuario.getStatus() != null && usuario.getStatus() == 1) {
            usuarioExistente.setStatus(1);
        } else {
            usuarioExistente.setStatus(0);
        }

        usuarioService.crearOEditar(usuarioExistente);

        redirect.addFlashAttribute("msg", "Usuario actualizado exitosamente");
        return "redirect:/usuarios";
    }


    // -------------------- ELIMINAR USUARIO --------------------
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        usuarioService.eliminarPorId(id);
        redirect.addFlashAttribute("msg", "Usuario eliminado exitosamente");
        return "redirect:/usuarios";
    }

    // -------------------- VISTA DE CONFIRMACIÓN DE ELIMINACIÓN --------------------
    @GetMapping("/delete-confirm/{id}")
    public String showDeleteConfirmation(@PathVariable("id") Integer id, Model model) {
        Usuario usuario = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        model.addAttribute("usuario", usuario);
        return "usuario/delete";
    }
}