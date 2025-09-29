package org.esfr.BazarBEG.controladores;


import org.esfr.BazarBEG.modelos.dtos.carrito.CarritoItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final WebClient webClient;

    @Autowired
    public CarritoController(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Maneja la solicitud GET para mostrar la página del carrito.
     * @param model El modelo para pasar datos a la vista.
     * @return El nombre de la vista de carrito.
     */
    @GetMapping
    public String verCarrito(Model model) {
        // En un entorno real, el ID del usuario se obtendría del contexto de seguridad o sesión.
        // Aquí se usa un valor estático para el ejemplo.
        Long idUsuario = 1L;

        // Realiza una llamada GET a la API del backend para obtener los productos del carrito.
        Mono<List<CarritoItemDTO>> carritoMono = webClient.get()
                .uri("/v1/carrito/{idUsuario}", idUsuario)
                .retrieve()
                .bodyToFlux(CarritoItemDTO.class)
                .collectList();

        List<CarritoItemDTO> carritoItems = carritoMono.block(); // .block() simplifica la ejecución, pero no es ideal para entornos de producción.

        // Calcula el total del carrito sumando los precios de los productos.
        double total = 0.0;
        if (carritoItems != null) {
            total = carritoItems.stream()
                    .mapToDouble(item -> item.getPrecio().doubleValue() * item.getCantidad())
                    .sum();
        }

        // Agrega los datos al modelo para que la vista los pueda renderizar.
        model.addAttribute("carritoItems", carritoItems);
        model.addAttribute("total", total);
        return "carrito/carrito";
    }

}