package org.esfr.BazarBEG.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private RoleBasedRedirectHandler roleBasedRedirectHandler;

    @Autowired
    private ApiAuthenticationProvider apiAuthenticationProvider;

    @Autowired
    private  JwtAuthenticationFilter jwtAuthFilter;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(apiAuthenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas para todos
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/login", "/registro", "/loginVerify").permitAll()
                        .requestMatchers("/catalogo/**").permitAll()
                        .requestMatchers("/categorias/imagen/**", "/productos/imagen/**").permitAll()

                        // Rutas protegidas para CLIENTES
                        .requestMatchers("/pedidos-cliente/historial","/pedidos-cliente/factura/**", "/perfil/**", "/carrito/**", "/checkout/**").hasAnyRole("CLIENTE", "ADMINISTRADOR")

                        // Rutas protegidas para ADMINISTRADOR
                        .requestMatchers("/admin/**", "/usuarios/**", "/roles/**", "/categorias/**", "/productos/**", "/pedidos-cliente/**").hasRole("ADMINISTRADOR")


                        // Carrito de compras protegido
                        .requestMatchers("/carrito/**", "/perfil/**").authenticated()

                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .usernameParameter("email")
                        .successHandler(roleBasedRedirectHandler)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }


}