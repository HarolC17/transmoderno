package com.gimnasio.transmoderno.auth.domain.usecase;

import com.gimnasio.transmoderno.auth.domain.exception.CredencialesInvalidasException;
import com.gimnasio.transmoderno.auth.domain.model.Rol;
import com.gimnasio.transmoderno.auth.domain.model.Usuario;
import com.gimnasio.transmoderno.auth.domain.model.port.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LoginUseCase useCase;

    @Test
    void debeRetornarUsuarioCuandoCorreoExiste() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .correo("admin@gimnasio.com")
                .nombre("Administrador")
                .rol(Rol.ADMIN)
                .activo(true)
                .build();

        when(usuarioRepository.findByCorreo("admin@gimnasio.com"))
                .thenReturn(Optional.of(usuario));

        Usuario resultado = useCase.ejecutar("admin@gimnasio.com");

        assertNotNull(resultado);
        assertEquals("admin@gimnasio.com", resultado.getCorreo());
        assertEquals(Rol.ADMIN, resultado.getRol());
    }

    @Test
    void debeLanzarExcepcionSiCorreoNoExiste() {
        when(usuarioRepository.findByCorreo("noexiste@gimnasio.com"))
                .thenReturn(Optional.empty());

        assertThrows(CredencialesInvalidasException.class,
                () -> useCase.ejecutar("noexiste@gimnasio.com"));
    }
}