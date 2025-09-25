package org.esfr.BazarBEG.repositorios;

import jakarta.servlet.http.HttpSession;
import org.esfr.BazarBEG.modelos.dtos.roles.Role;
import org.esfr.BazarBEG.modelos.dtos.usuarios.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class RolRepository {
    private final WebClient webClient;

    public RolRepository(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("https://apiloginregistro-1.onrender.com").build();
    }

    @Cacheable("roles")
    public List<Role> obtenerTodosRoles() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String token = (session != null) ? (String) session.getAttribute("JWT_TOKEN") : null;

        if (token == null) {
            throw new IllegalStateException("No se encontró JWT");
        }

        return this.webClient.get()
                .uri("/roles")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(Role.class)
                .collectList()
                .block();
    }

}
