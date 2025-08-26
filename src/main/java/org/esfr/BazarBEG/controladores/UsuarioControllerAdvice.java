package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class UsuarioControllerAdvice {

    @Autowired
    private IUsuarioService usuarioService;

    @ModelAttribute("usuario")
    public Usuario getUsuario(Principal principal) {
        if (principal != null) {
            Optional<Usuario> usuarioOptional = usuarioService.obtenerPorEmail(principal.getName());
            return usuarioOptional.orElse(null);
        }
        return null;
    }
}