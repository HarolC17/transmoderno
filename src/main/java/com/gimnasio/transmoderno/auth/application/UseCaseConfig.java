package com.gimnasio.transmoderno.auth.application;

import com.gimnasio.transmoderno.auth.domain.model.port.UsuarioRepository;
import com.gimnasio.transmoderno.auth.domain.usecase.LoginUseCase;
import com.gimnasio.transmoderno.auth.domain.usecase.ObtenerUsuariosUseCase;
import com.gimnasio.transmoderno.auth.domain.usecase.RegistrarUsuarioUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public LoginUseCase loginUseCase(UsuarioRepository usuarioRepository) {
        return new LoginUseCase(usuarioRepository);
    }

    @Bean
    public RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        return new RegistrarUsuarioUseCase(usuarioRepository);
    }

    @Bean
    public ObtenerUsuariosUseCase obtenerUsuariosUseCase(UsuarioRepository usuarioRepository) {
        return new ObtenerUsuariosUseCase(usuarioRepository);
    }
}