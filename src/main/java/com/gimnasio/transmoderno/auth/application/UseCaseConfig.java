package com.gimnasio.transmoderno.auth.application;

import com.gimnasio.transmoderno.auth.domain.model.port.UsuarioRepository;
import com.gimnasio.transmoderno.auth.domain.usecase.LoginUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public LoginUseCase loginUseCase(UsuarioRepository usuarioRepository) {
        return new LoginUseCase(usuarioRepository);
    }
}