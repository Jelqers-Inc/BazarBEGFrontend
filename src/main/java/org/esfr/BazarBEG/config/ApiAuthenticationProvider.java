package org.esfr.BazarBEG.config;

import org.esfr.BazarBEG.modelos.dtos.usuarios.LoginResponse;
import org.esfr.BazarBEG.servicios.implementaciones.UsuarioService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApiAuthenticationProvider implements AuthenticationProvider {
    private final UsuarioService usuarioService;

    public ApiAuthenticationProvider(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        LoginResponse loginResponse = usuarioService.loginObtener(username, password);

        if (loginResponse != null && loginResponse.getAccessToken() != null) {
            List<SimpleGrantedAuthority> authorities = mapRoles(loginResponse.getUserPayload().getRolId());

            UsernamePasswordAuthenticationToken authResult = new UsernamePasswordAuthenticationToken(
                    loginResponse.getUserPayload(),
                    null,
                    authorities
            );

            // *** Add the token to the details property ***
            authResult.setDetails(loginResponse.getAccessToken());

            return authResult;
        }
        else {
            throw new BadCredentialsException("Authentication failed for user: " + username);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public List<SimpleGrantedAuthority> mapRoles(int rolId) {
        if (rolId == 1) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMINISTRADOR"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"));
    }
}
