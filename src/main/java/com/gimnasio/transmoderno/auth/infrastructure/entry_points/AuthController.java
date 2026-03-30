package com.gimnasio.transmoderno.auth.infrastructure.entry_points;

import com.gimnasio.transmoderno.auth.domain.exception.CredencialesInvalidasException;
import com.gimnasio.transmoderno.auth.domain.model.Usuario;
import com.gimnasio.transmoderno.auth.domain.usecase.LoginUseCase;
import com.gimnasio.transmoderno.auth.infrastructure.entry_points.dto.LoginRequest;
import com.gimnasio.transmoderno.auth.infrastructure.entry_points.dto.LoginResponse;
import com.gimnasio.transmoderno.auth.infrastructure.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = loginUseCase.ejecutar(request.getCorreo());

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new CredencialesInvalidasException();
        }

        String token = jwtService.generarToken(usuario.getCorreo(), "ADMIN");

        return ResponseEntity.ok(new LoginResponse(
                token,
                usuario.getNombre(),
                usuario.getCorreo()
        ));
    }
}