package org.esfr.BazarBEG.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler roleBasedRedirectHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Rutas públicas para todos
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/login", "/registro").permitAll()
                        .requestMatchers("/catalogo/**").permitAll()
                        .requestMatchers("/categorias/imagen/**", "/productos/imagen/**").permitAll()

                        // Rutas protegidas para CLIENTES
                        .requestMatchers("/pedidos-cliente/historial", "/perfil/**", "/carrito/**", "/checkout/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")

                        // Rutas protegidas para ADMINISTRADOR
                        .requestMatchers("/admin/**", "/usuarios/**", "/roles/**", "/categorias/**", "/productos/**", "/pedidos-cliente/**").hasRole("ADMINISTRADOR")


                        // Carrito de compras protegido
                        .requestMatchers("/carrito/**", "/perfil/**").authenticated()

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(roleBasedRedirectHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}