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

    // 1. Inyecta la nueva clase de redirección
    @Autowired
    private AuthenticationSuccessHandler roleBasedRedirectHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // Rutas públicas que no requieren autenticación
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/", "/login", "/registro", "/catalogo").permitAll()

                        // Rutas protegidas que requieren roles específicos
                        .requestMatchers("/admin/**", "/usuarios/**", "/roles/**", "/categorias/**", "/productos/**", "/pedidos/**").hasAnyRole("ADMINISTRADOR")
                        .requestMatchers("/catalogo", "/catalogo-cliente").hasAnyRole("CLIENTE", "ADMINISTRADOR")
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        // 2. Utiliza el successHandler para la redirección condicional
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