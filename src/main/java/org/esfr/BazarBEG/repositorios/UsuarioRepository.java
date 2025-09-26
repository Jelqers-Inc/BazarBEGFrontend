package org.esfr.BazarBEG.repositorios;

import jakarta.servlet.http.HttpSession;
import org.esfr.BazarBEG.modelos.dtos.usuarios.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UsuarioRepository {
    private final WebClient webClient;

    public UsuarioRepository(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("https://apiloginregistro-1.onrender.com").build();
    }


    @Cacheable("usuarios")
    public List<User> obtenerTodosUsuarios() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String token = (session != null) ? (String) session.getAttribute("JWT_TOKEN") : null;

        if (token == null) {
            throw new IllegalStateException("No se encontró JWT");
        }

        return this.webClient.get()
                .uri("/usuarios")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(User.class)
                .collectList()
                .block();
    }

    public User obtenerPorEmail(String email) {
        return this.webClient.get()
                .uri("/usuarios/email/{email}", email)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.empty())
                .bodyToMono(User.class)
                .block();
    }

    public User obtenerPorId(Integer id) {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String token = (session != null) ? (String) session.getAttribute("JWT_TOKEN") : null;

        return this.webClient.get()
                .uri("/usuarios/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(status -> status.equals(HttpStatus.NOT_FOUND),
                        response -> Mono.empty())
                .bodyToMono(User.class)
                .block();
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public User crear(UserCreate userCreate){
        return  this.webClient.post()
                .uri("/usuarios")
                .body(Mono.just(userCreate), UserCreate.class)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public User actualizar(UserUpdate user) {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String token = (session != null) ? (String) session.getAttribute("JWT_TOKEN") : null;

        if (token == null) {
            throw new IllegalStateException("No se encontró JWT");
        }

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("nombre", user.getNombre());
        builder.part("apellido", user.getApellido());
        builder.part("email", user.getEmail());
        builder.part("rol", user.getRolId().toString());

        if (user.getFoto() != null) {
            builder.part("foto", new ByteArrayResource(user.getFoto()))
                    .filename("profile.png");
        }

        return this.webClient.put()
                .uri("/usuarios/{id}", user.getId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }


    public LoginResponse loginObtenerToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);

        LoginResponse loginResponse = this.webClient.post()
                .uri("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(LoginResponse.class)
                .block();

        if (loginResponse != null && loginResponse.getAccessToken() != null) {
            return loginResponse;
        }
        return  null;

    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void eliminar(Integer id) {

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String token = (session != null) ? (String) session.getAttribute("JWT_TOKEN") : null;

        this.webClient.delete()
                .uri("/usuarios/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }



}

