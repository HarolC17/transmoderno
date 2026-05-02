package com.gimnasio.transmoderno.auth.infrastructure.config;

import com.gimnasio.transmoderno.auth.infrastructure.security.JwtAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import java.util.List;

import com.gimnasio.transmoderno.auth.domain.model.port.UsuarioRepository;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UsuarioRepository usuarioRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173",
                            "http://192.168.10.13:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    config.setMaxAge(3600L);
                    return config;
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Auth — solo login es público
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                        // Participantes — registro y consulta pública
                        .requestMatchers(HttpMethod.POST, "/api/participantes").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/participantes/identificacion/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/participantes/ucundinamarca/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/participantes/programas").permitAll()

                        // Rutas — catálogo público
                        .requestMatchers(HttpMethod.GET, "/api/rutas/**").permitAll()

                        // Inscripciones — solo lo que hace el estudiante
                        .requestMatchers(HttpMethod.POST, "/api/inscripciones").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/inscripciones/participante/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/inscripciones/*/cancelar").permitAll()

                        // Sesiones — solo consulta pública (QR y sesión activa)
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/activa/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/sesiones/*").permitAll()

                        // Fichas — estudiante las diligencia sin token
                        .requestMatchers(HttpMethod.POST, "/api/fichas/pre").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/fichas/post").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/preguntas/ruta/**").permitAll()

                        // Alertas — solo levantar la mano es público
                        .requestMatchers(HttpMethod.POST, "/api/alertas/ayuda").permitAll()

                        // Asistencia — solo registro y consulta propia son públicos
                        .requestMatchers(HttpMethod.POST, "/api/asistencia").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/asistencia/participante/**").permitAll()

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"mensaje\": \"Acceso denegado\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return correo -> usuarioRepository.findByCorreo(correo)
                .map(usuario -> org.springframework.security.core.userdetails.User
                        .withUsername(usuario.getCorreo())
                        .password(usuario.getContrasena())
                        .roles(usuario.getRol().name())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}