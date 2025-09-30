package org.esfr.BazarBEG.repositorios;

import jakarta.servlet.http.HttpSession;
import org.esfr.BazarBEG.modelos.dtos.carrito.CarritoInsert;
import org.esfr.BazarBEG.modelos.dtos.carrito.CarritoItemDTO;
import org.esfr.BazarBEG.servicios.implementaciones.JWTService;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CarritoReopsitory {

    private final WebClient webClient;

    private final JWTService jwtService;

    public CarritoReopsitory(WebClient.Builder webClient, JWTService jwtService) {
        this.webClient = webClient.baseUrl("https://apicliente-xkew.onrender.com/api/v1").build();
        this.jwtService = jwtService;
    }

    private String getToken() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(false);
        String token = (session != null) ? (String) session.getAttribute("JWT_TOKEN") : null;

        if (token == null) {
            throw new IllegalStateException("No se encontró JWT para la petición de Categoría");
        }
        return token;
    }

    public List<CarritoItemDTO> obtenerTodo() {
        String token = getToken();
        Integer idUser = jwtService.extractClaim(token, claim -> claim.get("id", Integer.class));

        return this.webClient.get()
                .uri("/carrito/{id}", idUser)
                .retrieve()
                .bodyToFlux(CarritoItemDTO.class)
                .collectList()
                .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                .block();
    }



    public String crear(Integer id, Integer cantidad) {
        String token = getToken();
        Integer idUser = jwtService.extractClaim(token, claim -> claim.get("id", Integer.class));
        CarritoInsert carritoInsert = new CarritoInsert();
        carritoInsert.setIdProducto(id);
        carritoInsert.setIdUsuario(idUser);
        carritoInsert.setCantidad(cantidad);

        return this.webClient.post()
                .uri("/carrito")
                .bodyValue(carritoInsert)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }


//    public Categoriadto actualizar(Categoriadto categoria) {
//        String token = getToken();
//
//        MultipartBodyBuilder builder = new MultipartBodyBuilder();
//
//        builder.part("nombre", categoria.getNombre());
//
//        if (categoria.getImagen() != null) {
//            builder.part("imagen", new ByteArrayResource(categoria.getImagen()))
//                    .filename("category.png");
//        }
//
//        return this.webClient.put()
//                .uri("/categorias/{id}", categoria.getId())
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
//                .body(BodyInserters.fromMultipartData(builder.build()))
//                .retrieve()
//                .bodyToMono(Categoriadto.class)
//                .block();
//    }

    public void eliminar(Integer id) {

        this.webClient.delete()
                .uri("/carrito/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
