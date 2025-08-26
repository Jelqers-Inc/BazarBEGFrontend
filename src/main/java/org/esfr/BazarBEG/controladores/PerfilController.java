package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private IUsuarioService usuarioService;

    private final Path directorioSubidas = Paths.get("src/main/resources/static/uploads/perfiles");

    @GetMapping
    public String verPerfil(Model model, Principal principal) {
        if (principal != null) {
            Optional<Usuario> usuarioOptional = usuarioService.obtenerPorEmail(principal.getName());
            usuarioOptional.ifPresent(usuario -> model.addAttribute("usuario", usuario));
        }
        return "perfil/perfil";
    }

    @PostMapping("/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }
        if (!foto.isEmpty()) {
            try {
                Optional<Usuario> usuarioOptional = usuarioService.obtenerPorEmail(principal.getName());
                if (usuarioOptional.isPresent()) {
                    Usuario usuario = usuarioOptional.get();
                    if (!Files.exists(directorioSubidas)) {
                        Files.createDirectories(directorioSubidas);
                    }
                    // Elimina la foto anterior si existe
                    if (usuario.getFoto() != null && !usuario.getFoto().isEmpty()) {
                        Files.deleteIfExists(directorioSubidas.resolve(usuario.getFoto()));
                    }
                    String nombreArchivo = usuario.getId() + "-" + foto.getOriginalFilename();
                    Path rutaCompleta = this.directorioSubidas.resolve(nombreArchivo);
                    Files.write(rutaCompleta, foto.getBytes());
                    usuario.setFoto(nombreArchivo);
                    usuarioService.crearOEditar(usuario);
                    redirectAttributes.addFlashAttribute("fotoSubidaExitosamente", true);
                }
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorSubida", "Hubo un error al subir la foto.");
            }
        }
        return "redirect:/perfil";
    }

    @GetMapping("/imagen/{id}")
    public ResponseEntity<Resource> getImagen(@PathVariable Integer id) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(id);
        Path archivo;
        if (usuarioOptional.isPresent() && usuarioOptional.get().getFoto() != null) {
            archivo = directorioSubidas.resolve(usuarioOptional.get().getFoto()).normalize();
        } else {
            // Imagen por defecto si el usuario no tiene foto
            archivo = Paths.get("src/main/resources/static/images/perfil_placeholder.png").normalize();
        }
        try {
            Resource recurso = new UrlResource(archivo.toUri());
            if (recurso.exists() && recurso.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(archivo))
                        .body(recurso);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/editar")
    public String editarPerfil(@ModelAttribute("usuario") Usuario usuarioActualizado, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOptional = usuarioService.buscarPorId(usuarioActualizado.getId());
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            usuario.setNombre(usuarioActualizado.getNombre());
            usuario.setApellido(usuarioActualizado.getApellido());
            usuario.setEmail(usuarioActualizado.getEmail());
            usuarioService.crearOEditar(usuario);
            redirectAttributes.addFlashAttribute("exitoEdicion", "Perfil actualizado correctamente.");
        }
        return "redirect:/perfil";
    }

    @GetMapping("/eliminar")
    public String eliminarPerfil(Principal principal, RedirectAttributes redirectAttributes) {
        if (principal != null) {
            Optional<Usuario> usuarioOptional = usuarioService.obtenerPorEmail(principal.getName());
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                try {
                    // Elimina la foto del sistema de archivos
                    if (usuario.getFoto() != null && !usuario.getFoto().isEmpty()) {
                        Files.deleteIfExists(directorioSubidas.resolve(usuario.getFoto()));
                    }
                    // Elimina el usuario de la base de datos
                    usuarioService.eliminarPorId(usuario.getId());
                    // Redirige al inicio o a una página de confirmación
                    return "redirect:/logout"; // Se recomienda cerrar la sesión después de eliminar
                } catch (IOException e) {
                    redirectAttributes.addFlashAttribute("errorEliminacion", "Hubo un error al eliminar el perfil.");
                }
            }
        }
        return "redirect:/perfil";
    }
}