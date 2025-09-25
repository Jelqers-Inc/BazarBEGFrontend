package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.modelos.dtos.usuarios.UserUpdate;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
        if (model.getAttribute("usuarioLogueado") != null) {
            model.addAttribute("usuario", model.getAttribute("usuarioLogueado"));
        }
        return "perfil/perfil";
    }

    @PostMapping("/subirFoto")
    public String subirFoto(@RequestParam("foto") MultipartFile foto ,
                            Principal principal, RedirectAttributes redirectAttributes, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        if (!foto.isEmpty()) {
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
        }
        return "redirect:/perfil";
   }

    @GetMapping(value = "/imagen", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getImagen(Model model) {
        User user = (User) model.getAttribute("usuarioLogueado");
        if (user != null && user.getFoto() != null && user.getFoto().getData().length > 0) {
            return ResponseEntity.ok().body(user.getFoto().getData());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/editar")
    public String editarPerfil(@ModelAttribute("usuario") Usuario usuarioActualizado, Model model, RedirectAttributes redirectAttributes) {
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
        return "redirect:/perfil";
    }

//    @GetMapping("/eliminar")
//    public String eliminarPerfil(Principal principal, RedirectAttributes redirectAttributes) {
//        if (principal != null) {
//            Optional<Usuario> usuarioOptional = usuarioService.obtenerPorEmail(principal.getName());
//            if (usuarioOptional.isPresent()) {
//                Usuario usuario = usuarioOptional.get();
//                try {
//                    // Elimina la foto del sistema de archivos
//                    if (usuario.getFoto() != null && !usuario.getFoto().isEmpty()) {
//                        Files.deleteIfExists(directorioSubidas.resolve(usuario.getFoto()));
//                    }
//                    // Elimina el usuario de la base de datos
//                    usuarioService.eliminarPorId(usuario.getId());
//                    // Redirige al inicio o a una página de confirmación
//                    return "redirect:/logout"; // Se recomienda cerrar la sesión después de eliminar
//                } catch (IOException e) {
//                    redirectAttributes.addFlashAttribute("errorEliminacion", "Hubo un error al eliminar el perfil.");
//                }
//            }
//        }
//        return "redirect:/perfil";
//    }
}