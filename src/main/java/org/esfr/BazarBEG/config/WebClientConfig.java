package org.esfr.BazarBEG.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {

        final int size = 16 * 1024 * 1024;

        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(size))
                .build();

        HttpClient httpClient = HttpClient.create();

        //Estrategias
        return WebClient.builder()
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient));


    }

    /**
     * 2. Bean principal de WebClient para la API del Carrito.
     * Este método toma el WebClient.Builder configurado (del punto 1)
     * y le añade la URL base específica de tu API de carrito antes de construirlo.
     */
    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        // La URL base es la dirección de tu API REST de carrito (backend)
        return webClientBuilder
                .baseUrl("https://apicliente-xkew.onrender.com/api")
                .build();
    }
}
