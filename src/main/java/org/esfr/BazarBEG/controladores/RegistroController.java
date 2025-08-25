package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.Rol;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.esfr.BazarBEG.repositorios.IRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IRolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping
    public String procesarRegistro(Usuario usuario, RedirectAttributes redirectAttributes) {
        if (usuarioService.obtenerPorEmail(usuario.getEmail()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo electrónico ya está registrado.");
            return "redirect:/registro";
        }

        Optional<Rol> rolClienteOpt = rolRepository.findByNombre("CLIENTE");
        if (rolClienteOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Error interno: Rol de cliente no encontrado.");
            return "redirect:/registro";
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(rolClienteOpt.get());
        usuario.setStatus(1);
        usuarioService.crearOEditar(usuario);

        redirectAttributes.addFlashAttribute("msg", "¡Registro exitoso! Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}