package org.esfr.BazarBEG.config;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.esfr.BazarBEG.repositorios.UsuarioRepository;
import org.esfr.BazarBEG.servicios.implementaciones.JWTService;
import org.esfr.BazarBEG.servicios.implementaciones.UsuarioService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // You will need a service to handle JWT parsing and validation
    // and a service to load user details from your database or cache.
    private final JWTService jwtService;
    private final UsuarioService userDetailsService;
    private final UsuarioRepository userRepository;

    public JwtAuthenticationFilter(JWTService jwtService, UsuarioService userDetailsService, UsuarioRepository userRepository) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = null;
        final String userEmail;

        // --- MODIFIED SECTION ---
        // 1. Try to get the token from the HttpSession
        HttpSession session = request.getSession(false); // 'false' means don't create a new session
        if (session != null && session.getAttribute("JWT_TOKEN") != null) {
            jwt = (String) session.getAttribute("JWT_TOKEN");
        }
        // 2. If not in session, fall back to the Authorization header
        else {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
        }
        // --- END MODIFIED SECTION ---

        if (jwt == null || jwt.isBlank()) {
            filterChain.doFilter(request, response);
            return; // Stop processing if no token is found
        }


        try{
    // 3. Extract the user's email (or username) from the token
                userEmail = jwtService.extractUsername(jwt);

                // 4. If we have a username and the user is not already authenticated
                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Load the user details from your user service
                    final Integer roleId = jwtService.extractRoleId(jwt);
                    List<SimpleGrantedAuthority> authorities = mapRoles(roleId);

                    // 2. Create a UserDetails object without hitting the database
                    UserDetails userDetails = new User(userEmail, "", authorities);

                    // 5. If the token is valid, update the SecurityContextHolder
                    if (jwtService.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                authorities
                        );
                        authToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
        }
        catch (JwtException ex){
            System.out.println("Invalid JWT received: " + ex.getMessage());
        }

            filterChain.doFilter(request, response);

    }

    public List<SimpleGrantedAuthority> mapRoles(int rolId) {
        if (rolId == 1) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
}
