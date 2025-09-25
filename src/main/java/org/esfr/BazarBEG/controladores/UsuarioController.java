package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.modelos.dtos.usuarios.UserCreate;
import org.esfr.BazarBEG.modelos.dtos.usuarios.UserUpdate;
import org.esfr.BazarBEG.servicios.interfaces.IRolService;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
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

import jakarta.validation.Valid;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
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

    private final Path directorioSubidas = Paths.get("src/main/resources/static/uploads/perfiles");

    // -------------------- PÁGINA DE PERFIL DEL ADMINISTRADOR --------------------
    @GetMapping("/perfil")
    public String verPerfilAdmin(Model model, Principal principal) {
        if (model.getAttribute("usuarioLogueado") != null) {
            model.addAttribute("usuario", model.getAttribute("usuarioLogueado"));
            return "usuario/perfilAdmin";
        }
        return "redirect:/login";
    }

    // -------------------- SUBIR FOTO DE PERFIL DEL ADMIN --------------------
    @PostMapping("/perfil/subirFoto")
    public String subirFotoAdmin(@RequestParam("foto") MultipartFile foto,
                                 Principal principal, RedirectAttributes redirectAttributes,
                                 Model model) {
        if (principal == null || foto.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorSubida", "No se seleccionó una foto.");
            return "redirect:/usuarios/perfil";
        }
        try {
            User user = (User) model.getAttribute("usuarioLogueado");
            if (user.getEmail() != null) {
                if(user.getApellido() == null){
                    user.setApellido(user.getNombre());
                }
                UserUpdate update = new UserUpdate();
                update.setId(user.getId());
                update.setNombre(user.getNombre());
                update.setApellido(user.getApellido());
                update.setEmail(user.getEmail());
                update.setRolId(user.getRolId());
                update.setFoto(foto.getBytes());
                usuarioService.editar(update);
                redirectAttributes.addFlashAttribute("fotoSubidaExitosamente", true);
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorSubida", "Hubo un error al subir la foto.");
        }
        return "redirect:/usuarios/perfil";
    }

    // -------------------- SERVIR IMAGEN DE PERFIL DEL ADMIN --------------------
    @GetMapping( value = "/perfil/imagen", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getImagenAdmin(Model model) {
        User user = (User) model.getAttribute("usuarioLogueado");
        if (user != null && user.getFoto() != null && user.getFoto().getData().length > 0) {
            return ResponseEntity.ok().body(user.getFoto().getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // -------------------- ACTUALIZAR PERFIL DEL ADMIN --------------------
    @PostMapping("/perfil/editar")
    public String editarPerfilAdmin(@ModelAttribute("usuario") Usuario usuarioActualizado, Model model, RedirectAttributes redirectAttributes) {
        User user = (User) model.getAttribute("usuarioLogueado");
        if (user.getId() != null) {
            if(user.getApellido() == null){
                user.setApellido(user.getNombre());
            }
            UserUpdate update = new UserUpdate();
            update.setId(user.getId());
            update.setNombre(usuarioActualizado.getNombre());
            update.setApellido(usuarioActualizado.getApellido());
            update.setEmail(usuarioActualizado.getEmail());
            update.setRolId(user.getRolId());
            update.setFoto(user.getFoto().getData());
            usuarioService.editar(update);
            redirectAttributes.addFlashAttribute("exitoEdicion", "Perfil actualizado correctamente.");
        }
        return "redirect:/usuarios/perfil";
    }


    // -------------------- LISTADO CON PAGINACIÓN Y BÚSQUEDA --------------------
    @GetMapping
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> page,
                        @RequestParam("size") Optional<Integer> size,
                        @RequestParam("q") Optional<String> query) {

        List<User> todosLosUsuarios = usuarioService.obtenerUsuarios();

        String searchQuery = query.orElse("").toLowerCase().trim();

        List<User> usuariosFiltrados;
        if (searchQuery.isBlank()) {
            usuariosFiltrados = todosLosUsuarios;
        } else {
            usuariosFiltrados = todosLosUsuarios.stream()
                    .filter(user -> user.getNombre().toLowerCase().contains(searchQuery)
                            || user.getEmail().toLowerCase().contains(searchQuery))
                    .collect(Collectors.toList());
        }

        int currentPage = page.orElse(1) - 1;
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(currentPage, pageSize);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), usuariosFiltrados.size());

        List<User> pageContent = List.of();
        if (start < usuariosFiltrados.size()) {
            pageContent = usuariosFiltrados.subList(start, end);
        }

        Page<User> usuarios = new PageImpl<>(pageContent, pageable, usuariosFiltrados.size());

        // 4. The rest of your model logic can stay the same
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("query", searchQuery);
        model.addAttribute("roles", rolService.obtenerTodos());

        int totalPages = usuarios.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "usuario/index";
    }


    // -------------------- MOSTRAR FORMULARIO DE CREACIÓN --------------------
    @GetMapping("/create")
    public String create(Model model) {
        UserCreate usuario = new UserCreate();
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/create";
    }


    // -------------------- GUARDAR NUEVO USUARIO --------------------
    @PostMapping("/save")
    public String save(@ModelAttribute UserCreate usuario, BindingResult result, RedirectAttributes redirect, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.obtenerTodos());
            return "usuario/create";
        }

        if(usuario.getApellido() == null){
            usuario.setApellido(usuario.getNombre());
        }

        usuarioService.crear(usuario);
        redirect.addFlashAttribute("msg", "Usuario guardado exitosamente");
        return "redirect:/usuarios";
    }

    // -------------------- DETALLES DE UN USUARIO --------------------
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model) {
        User usuario = usuarioService.obtenerPorId(id);
        model.addAttribute("usuario", usuario);
        return "usuario/details";
    }

    // -------------------- MOSTRAR FORMULARIO DE EDICIÓN --------------------
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model) {
        User usuario = usuarioService.obtenerPorId(id);
        UserUpdate userUpdate = new UserUpdate();
        userUpdate.setId(usuario.getId());
        userUpdate.setRolId(usuario.getRolId());
        if(usuario.getApellido() == null){
            userUpdate.setApellido(userUpdate.getNombre());
        }
        else {
            userUpdate.setApellido(usuario.getApellido());
        }
        userUpdate.setNombre(usuario.getNombre());
        userUpdate.setEmail(usuario.getEmail());

        model.addAttribute("usuario", userUpdate);
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/edit";
    }

    // -------------------- ACTUALIZAR USUARIO --------------------
    @PostMapping("/update/{id}")
    public String update(@PathVariable("id") Integer id, @ModelAttribute UserUpdate usuario,
                         BindingResult result, RedirectAttributes redirect, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("roles", rolService.obtenerTodos());
            return "usuario/edit";
        }

        User usuarioExistente = usuarioService.obtenerPorId(id);
        usuario.setId(usuarioExistente.getId());
        usuario.setFoto(usuarioExistente.getFoto().getData());

        usuarioService.editar(usuario);


        redirect.addFlashAttribute("msg", "Usuario actualizado exitosamente");
        return "redirect:/usuarios";
    }


    // -------------------- ELIMINAR USUARIO --------------------
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirect) {
        usuarioService.eliminar(id);
        redirect.addFlashAttribute("msg", "Usuario eliminado exitosamente");
        return "redirect:/usuarios";
    }

    // -------------------- VISTA DE CONFIRMACIÓN DE ELIMINACIÓN --------------------
    @GetMapping("/delete-confirm/{id}")
    public String showDeleteConfirmation(@PathVariable("id") Integer id, Model model) {
        User usuario = usuarioService.obtenerPorId(id);
        model.addAttribute("usuario", usuario);
        return "usuario/delete";
    }
}