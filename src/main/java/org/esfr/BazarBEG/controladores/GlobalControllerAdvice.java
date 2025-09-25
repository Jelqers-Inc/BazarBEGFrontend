package org.esfr.BazarBEG.controladores;

import org.esfr.BazarBEG.modelos.Usuario;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.esfr.BazarBEG.modelos.dtos.usuarios.UserPayload;
import org.esfr.BazarBEG.servicios.interfaces.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private IUsuarioService usuarioService;

    @ModelAttribute("usuarioLogueado")
    public User getUsuarioLogueado(Authentication authentication) { // <-- Change Principal to Authentication
        if (authentication != null && authentication.isAuthenticated()) {
            // 1. Get the raw principal object
            Object principal = authentication.getPrincipal();

            // 2. Check its type and cast it to UserPayload
            if (principal instanceof UserPayload) {
                UserPayload userPayload = (UserPayload) principal;

                // 3. Now you can correctly get the email
                String email = userPayload.getEmail();
                return usuarioService.obtenerPorEmail(email);
            }
        }
        return null;
    }
}