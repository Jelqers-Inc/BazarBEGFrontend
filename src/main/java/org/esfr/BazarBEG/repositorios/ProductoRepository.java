package org.esfr.BazarBEG.repositorios;

import jakarta.servlet.http.HttpSession;
import org.esfr.BazarBEG.modelos.dtos.categorias.Categoriadto;
import org.esfr.BazarBEG.modelos.dtos.productos.Product;
import org.esfr.BazarBEG.modelos.dtos.productos.ProductCreation;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
public class ProductoRepository {
    private final WebClient webClient;

    public ProductoRepository(WebClient.Builder webClient) {
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

    @Cacheable("productos")
    public List<Product> obtenerTodas() {
        String token = getToken();

        return this.webClient.get()
                .uri("/productos")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(Product.class)
                .collectList()
                .block();
    }

    // 💡 Método ÚNICO y CORREGIDO para obtener por ID con manejo de errores
    public Product obtenerPorId(Integer id) {

        try {
            return this.webClient.get()
                    .uri("/productos/{id}", id)
                    .retrieve()
                    .bodyToMono(Product.class)
                    .block();

        } catch (WebClientResponseException ex) {
            // Esto evita que un 502 de la API rompa tu aplicación cliente (que es lo que pasaba antes).
            System.err.println("Error al obtener Producto " + id + ". Código: " + ex.getStatusCode() + " - " + ex.getMessage());
            return null;
        }
    }

    public byte[] obtenerImagenPorId(Integer id) {

        try {
            return this.webClient.get()
                    .uri("/productos/{id}/imagen", id)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

        } catch (WebClientResponseException ex) {
            // Esto evita que un 502 de la API rompa tu aplicación cliente (que es lo que pasaba antes).
            System.err.println("Error al obtener Imágen " + id + ". Código: " + ex.getStatusCode() + " - " + ex.getMessage());
            return null;
        }
    }

    @CacheEvict(value = "productos", allEntries = true)
    public ProductCreation crear(ProductCreation producto) {
        String token = getToken();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("nombre", producto.getNombre());
        builder.part("descripcion", producto.getDescripcion());
        builder.part("precio", producto.getPrecio());
        builder.part("stock", producto.getStock());
        builder.part("categoria_id", producto.getCategoriaId());

        if (producto.getImagen() != null) {
            builder.part("imagen", new ByteArrayResource(producto.getImagen()))
                    .filename("product.png");
        }

        return this.webClient.post()
                .uri("/productos")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ProductCreation.class)
                .block();
    }



    @CacheEvict(value = "productos", allEntries = true)
    public ProductCreation actualizar(ProductCreation producto) {
        String token = getToken();

        MultipartBodyBuilder builder = new MultipartBodyBuilder();

        builder.part("nombre", producto.getNombre());
        builder.part("descripcion", producto.getDescripcion());
        builder.part("precio", producto.getPrecio());
        builder.part("stock", producto.getStock());
        builder.part("status", producto.getStatus());
        builder.part("categoria_id", producto.getCategoriaId());

        if (producto.getImagen() != null) {
            builder.part("imagen", new ByteArrayResource(producto.getImagen()))
                    .filename("product.png");
        }

        return this.webClient.put()
                .uri("/productos/{id}", producto.getId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ProductCreation.class)
                .block();
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void eliminar(Integer id) {
        String token = getToken();

        this.webClient.delete()
                .uri("/productos/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
