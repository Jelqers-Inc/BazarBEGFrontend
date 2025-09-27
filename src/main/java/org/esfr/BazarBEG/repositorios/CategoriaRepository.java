package org.esfr.BazarBEG.repositorios;

import jakarta.servlet.http.HttpSession;
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException; // Asegúrate de importar esto

import java.util.List;

@Service
public class CategoriaRepository {
    private final WebClient webClient;

    public CategoriaRepository(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("https://apiadministrador.onrender.com").build();
    }

    // Método auxiliar para obtener el token JWT de la sesión
    private String getToken() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String token = (session != null) ? (String) session.getAttribute("JWT_TOKEN") : null;

        if (token == null) {
            throw new IllegalStateException("No se encontró JWT para la petición de Categoría");
        }
        return token;
    }

    @Cacheable("categorias")
    public List<Categoriadto> obtenerTodas() {
        String token = getToken();

        return this.webClient.get()
                .uri("/categorias")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(Categoriadto.class)
                .collectList()
                .block();
    }

    // 💡 Método ÚNICO y CORREGIDO para obtener por ID con manejo de errores
    @Cacheable("categorias")
    public Categoriadto obtenerPorId(Integer id) {
        String token = getToken();

        try {
            return this.webClient.get()
                    .uri("/categorias/{id}", id)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Categoriadto.class)
                    .block();

        } catch (WebClientResponseException ex) {
            // Esto evita que un 502 de la API rompa tu aplicación cliente (que es lo que pasaba antes).
            System.err.println("Error al obtener Categoría " + id + ". Código: " + ex.getStatusCode() + " - " + ex.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "categorias", allEntries = true)
    public Categoriadto crear(Categoriadto categoria) {
        String token = getToken();

        return this.webClient.post()
                .uri("/categorias")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(categoria)
                .retrieve()
                .bodyToMono(Categoriadto.class)
                .block();
    }

    @CacheEvict(value = "categorias", allEntries = true)
    public Categoriadto actualizar(Categoriadto categoria) {
        String token = getToken();

        return this.webClient.put()
                .uri("/categorias/{id}", categoria.getId())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(categoria)
                .retrieve()
                .bodyToMono(Categoriadto.class)
                .block();
    }

    @CacheEvict(value = "categorias", allEntries = true)
    public void eliminar(Integer id) {
        String token = getToken();

        this.webClient.delete()
                .uri("/categorias/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}