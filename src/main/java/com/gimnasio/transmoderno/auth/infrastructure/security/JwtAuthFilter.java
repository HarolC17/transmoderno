package com.gimnasio.transmoderno.auth.infrastructure.security;

import com.gimnasio.transmoderno.auth.domain.model.port.UsuarioRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String correo = jwtService.extraerCorreo(token);

            if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                usuarioRepository.findByCorreo(correo).ifPresent(usuario -> {
                    if (jwtService.validarToken(token, correo) && usuario.getActivo()) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        correo,
                                        null,
                                        List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
                                );
                        authToken.setDetails(usuario.getId());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                });
            }

            filterChain.doFilter(request, response);

        } catch (JwtException e) {
            // JwtException cubre tanto token vencido (ExpiredJwtException)
            // como token malformado o con firma inválida.
            // No establecer autenticación y continuar la cadena:
            //   - Endpoint público (permitAll) → Spring lo permite ✓
            //   - Endpoint protegido (ADMIN)   → Spring devuelve 401 → Axios redirige al login ✓
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // Error inesperado en el filtro → 500 controlado
            escribirError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error interno al procesar la autenticación.");
        }
    }

    private void escribirError(HttpServletResponse response, int status, String mensaje) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"mensaje\": \"" + mensaje + "\"}");
    }
}