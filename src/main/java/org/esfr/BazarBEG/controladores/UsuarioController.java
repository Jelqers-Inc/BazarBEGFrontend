package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
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
//    @PostMapping("/perfil/subirFoto")
//    public String subirFotoAdmin(@RequestParam("foto") MultipartFile foto, Principal principal, RedirectAttributes redirectAttributes) {
//        if (principal == null || foto.isEmpty()) {
//            redirectAttributes.addFlashAttribute("errorSubida", "No se seleccionó una foto.");
//            return "redirect:/usuarios/perfil";
//        }
//        try {
//            Optional<Usuario> usuarioOptional = usuarioService.obtenerPorEmail(principal.getName());
//            if (usuarioOptional.isPresent()) {
//                Usuario usuario = usuarioOptional.get();
//                if (!Files.exists(directorioSubidas)) {
//                    Files.createDirectories(directorioSubidas);
//                }
//                if (usuario.getFoto() != null && !usuario.getFoto().isEmpty()) {
//                    Files.deleteIfExists(directorioSubidas.resolve(usuario.getFoto()));
//                }
//                String nombreArchivo = usuario.getId() + "-" + foto.getOriginalFilename();
//                Path rutaCompleta = this.directorioSubidas.resolve(nombreArchivo);
//                Files.write(rutaCompleta, foto.getBytes());
//                usuario.setFoto(nombreArchivo);
//                usuarioService.crearOEditar(usuario);
//                redirectAttributes.addFlashAttribute("fotoSubidaExitosamente", true);
//            }
//        } catch (IOException e) {
//            redirectAttributes.addFlashAttribute("errorSubida", "Hubo un error al subir la foto.");
//        }
//        return "redirect:/usuarios/perfil";
//    }

    // -------------------- SERVIR IMAGEN DE PERFIL DEL ADMIN --------------------
//    @GetMapping("/perfil/imagen/{id}")
//    @ResponseBody
//    public ResponseEntity<Resource> getImagenAdmin(@PathVariable Integer id) {
//        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);
//        Path archivo;
//        if (usuarioOptional.isPresent() && usuarioOptional.get().getFoto() != null) {
//            archivo = directorioSubidas.resolve(usuarioOptional.get().getFoto()).normalize();
//        } else {
//            archivo = Paths.get("src/main/resources/static/images/perfil_placeholder_admin.png").normalize(); // Asegúrate de tener esta imagen
//        }
//        try {
//            Resource recurso = new UrlResource(archivo.toUri());
//            if (recurso.exists() && recurso.isReadable()) {
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(archivo))
//                        .body(recurso);
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return ResponseEntity.notFound().build();
//    }

    // -------------------- ACTUALIZAR PERFIL DEL ADMIN --------------------
//    @PostMapping("/perfil/editar")
//    public String editarPerfilAdmin(@ModelAttribute("usuario") Usuario usuarioActualizado, RedirectAttributes redirectAttributes) {
//        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(usuarioActualizado.getId());
//        if (usuarioOptional.isPresent()) {
//            Usuario usuario = usuarioOptional.get();
//            usuario.setNombre(usuarioActualizado.getNombre());
//            usuario.setApellido(usuarioActualizado.getApellido());
//            usuario.setEmail(usuarioActualizado.getEmail());
//            usuarioService.crearOEditar(usuario);
//            redirectAttributes.addFlashAttribute("exitoEdicion", "Perfil actualizado correctamente.");
//        }
//        return "redirect:/usuarios/perfil";
//    }


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
        Usuario usuario = new Usuario();
        usuario.setStatus(1);
        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", rolService.obtenerTodos());
        return "usuario/create";
    }


    // -------------------- GUARDAR NUEVO USUARIO --------------------
//    @PostMapping("/save")
//    public String save(@Valid @ModelAttribute Usuario usuario, BindingResult result, RedirectAttributes redirect, Model model) {
//        if (result.hasErrors()) {
//            model.addAttribute("roles", rolService.obtenerTodos());
//            return "usuario/create";
//        }
//
//        // Verificar rol seleccionado, si no asignar por defecto
//        if (usuario.getRol() != null && usuario.getRol() != 0) {
//            Integer rolSeleccionado = rolService.buscarPorId(usuario.getRol()).get().getId();
//            usuario.setRol(rolSeleccionado);
//        } else {
//            // Rol por defecto = CLIENTE (ID = 2)
//            Integer rolPorDefecto = 2;
//            usuario.setRol(rolPorDefecto);
//        }
//
//        usuarioService.crearOEditar(usuario);
//        redirect.addFlashAttribute("msg", "Usuario guardado exitosamente");
//        return "redirect:/usuarios";
//    }

    // -------------------- DETALLES DE UN USUARIO --------------------
//    @GetMapping("/details/{email}")
//    public String details(@PathVariable("id") Integer id, Model model) {
//        Usuario usuario = usuarioService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
//        model.addAttribute("usuario", usuario);
//        return "usuario/details";
//    }

    // -------------------- MOSTRAR FORMULARIO DE EDICIÓN --------------------
//    @GetMapping("/edit/{id}")
//    public String edit(@PathVariable("id") Integer id, Model model) {
//        Usuario usuario = usuarioService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
//        model.addAttribute("usuario", usuario);
//        model.addAttribute("roles", rolService.obtenerTodos());
//        return "usuario/edit";
//    }

    // -------------------- ACTUALIZAR USUARIO --------------------
//    @PostMapping("/update/{id}")
//    public String update(@PathVariable("id") Integer id, @ModelAttribute Usuario usuario,
//                         BindingResult result, RedirectAttributes redirect, Model model) {
//
//        if (result.hasErrors()) {
//            model.addAttribute("roles", rolService.obtenerTodos());
//            return "usuario/edit";
//        }
//
//        Usuario usuarioExistente = usuarioService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
//
//        usuarioExistente.setNombre(usuario.getNombre());
//        usuarioExistente.setEmail(usuario.getEmail());
//
//        if (usuario.getRol() != null && usuario.getRol().getId() != null) {
//            Rol rolSeleccionado = rolService.buscarPorId(usuario.getRol().getId())
//                    .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + usuario.getRol().getId()));
//            usuarioExistente.setRol(rolSeleccionado);
//        }

//        // Esta es la lógica clave: establecer el estado explícitamente a 1 o 0
//        if (usuario.getStatus() != null && usuario.getStatus() == 1) {
//            usuarioExistente.setStatus(1);
//        } else {
//            usuarioExistente.setStatus(0);
//        }
//
//        usuarioService.crearOEditar(usuarioExistente);
//
//        redirect.addFlashAttribute("msg", "Usuario actualizado exitosamente");
//        return "redirect:/usuarios";
//    }


    // -------------------- ELIMINAR USUARIO --------------------
//    @PostMapping("/delete/{id}")
//    public String delete(@PathVariable("id") Integer id, RedirectAttributes redirect) {
//        usuarioService.eliminarPorId(id);
//        redirect.addFlashAttribute("msg", "Usuario eliminado exitosamente");
//        return "redirect:/usuarios";
//    }
//
//    // -------------------- VISTA DE CONFIRMACIÓN DE ELIMINACIÓN --------------------
//    @GetMapping("/delete-confirm/{id}")
//    public String showDeleteConfirmation(@PathVariable("id") Integer id, Model model) {
//        Usuario usuario = usuarioService.buscarPorId(id)
//                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
//        model.addAttribute("usuario", usuario);
//        return "usuario/delete";
//    }
}