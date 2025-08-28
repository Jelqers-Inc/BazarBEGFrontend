package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private IUsuarioService usuarioService;

    @ModelAttribute("usuarioLogueado")
    public Optional<Usuario> getUsuarioLogueado(Principal principal) {
        if (principal != null) {
            return usuarioService.obtenerPorEmail(principal.getName());
        }
        return Optional.empty();
    }
}