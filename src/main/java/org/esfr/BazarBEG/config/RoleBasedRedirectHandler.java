package org.esfr.BazarBEG.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class RoleBasedRedirectHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 1. Get the JWT from the authentication details
        String token = (String) authentication.getDetails();

        // 2. Save the token in the session
        HttpSession session = request.getSession(true);
        session.setAttribute("JWT_TOKEN", token);

        // Obtener los roles del usuario autenticado
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String urlDestino = determineTargetUrl(authentication);

        redirectStrategy.sendRedirect(request, response, urlDestino);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"))) {
            return "/usuarios";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
            return "/";
        } else {
            throw new IllegalStateException("Rol no encontrado");
        }
    }
}
