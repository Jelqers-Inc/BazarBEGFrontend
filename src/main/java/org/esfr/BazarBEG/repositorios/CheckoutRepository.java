package org.esfr.BazarBEG.repositorios;

import jakarta.servlet.http.HttpSession;
import org.esfr.BazarBEG.modelos.dtos.checkout.CheckoutDto;
import org.esfr.BazarBEG.modelos.dtos.checkout.PayResponse;
import org.esfr.BazarBEG.servicios.implementaciones.JWTService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CheckoutRepository {

    private final WebClient webClient;

    private final JWTService jwtService;

    public CheckoutRepository(WebClient.Builder webClient, JWTService jwtService) {
        this.webClient = webClient.baseUrl("https://apipago-zvml.onrender.com/api/v1").build();
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

    public PayResponse crearPedido(CheckoutDto checkoutDto) {
        String token = getToken();
        checkoutDto.setId_usuario(jwtService.extractClaim(token, claim -> claim.get("id", Integer.class)));

        return this.webClient.post()
                .uri("/pagos/stripe/crear-intencion")
                .bodyValue(checkoutDto)
                .retrieve()
                .bodyToMono(PayResponse.class)
                .block();
    }

    public ResponseEntity<Void> realizarPago(String paymentIntentId) {

        return this.webClient.post()
                .uri("/pagos/stripe/confirmar-pago")
                .bodyValue(paymentIntentId)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
