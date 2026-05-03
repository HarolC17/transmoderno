package com.gimnasio.transmoderno.auth.domain.usecase;

import com.gimnasio.transmoderno.auth.domain.exception.CredencialesInvalidasException;
import com.gimnasio.transmoderno.auth.domain.exception.UsuarioNoEncontradoException;
import com.gimnasio.transmoderno.auth.domain.exception.UsuarioYaExisteException;
import com.gimnasio.transmoderno.auth.domain.model.Rol;
import com.gimnasio.transmoderno.auth.domain.model.Usuario;
import com.gimnasio.transmoderno.auth.domain.model.port.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks private RegistrarUsuarioUseCase registrarUsuarioUseCase;
    @InjectMocks private ActualizarUsuarioUseCase actualizarUsuarioUseCase;
    @InjectMocks private CambiarContrasenaUseCase cambiarContrasenaUseCase;
    @InjectMocks private DesactivarUsuarioUseCase desactivarUsuarioUseCase;
    @InjectMocks private ObtenerUsuariosUseCase obtenerUsuariosUseCase;
    @InjectMocks private LoginUseCase loginUseCase;

    // --- RegistrarUsuarioUseCase ---
    @Test
    void debeRegistrarUsuarioExitosamente() {
        Usuario u = Usuario.builder().correo("a@g.com").nombre("A").rol(Rol.ADMIN).activo(true).build();
        when(usuarioRepository.findByCorreo("a@g.com")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any())).thenReturn(u);

        Usuario resultado = registrarUsuarioUseCase.ejecutar(u);
        assertNotNull(resultado);
        verify(usuarioRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiCorreoYaExiste() {
        Usuario u = Usuario.builder().correo("a@g.com").build();
        when(usuarioRepository.findByCorreo("a@g.com")).thenReturn(Optional.of(u));

        assertThrows(UsuarioYaExisteException.class, () -> registrarUsuarioUseCase.ejecutar(u));
        verify(usuarioRepository, never()).save(any());
    }

    // --- ActualizarUsuarioUseCase ---
    @Test
    void debeActualizarUsuarioExitosamente() {
        Usuario existente = Usuario.builder().id(1L).correo("a@g.com").nombre("A").rol(Rol.ADMIN).activo(true).build();
        Usuario actualizado = Usuario.builder().correo("a@g.com").nombre("B").rol(Rol.ADMIN).build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any())).thenReturn(existente);

        Usuario resultado = actualizarUsuarioUseCase.ejecutar(1L, actualizado);
        assertNotNull(resultado);
        verify(usuarioRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExisteAlActualizar() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> actualizarUsuarioUseCase.ejecutar(99L, Usuario.builder().correo("x@g.com").build()));
    }

    // --- CambiarContrasenaUseCase ---
    @Test
    void debeCambiarContrasenaExitosamente() {
        Usuario u = Usuario.builder().id(1L).contrasena("old").activo(true).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenReturn(u);

        cambiarContrasenaUseCase.ejecutar(1L, "nueva");

        assertEquals("nueva", u.getContrasena());
        verify(usuarioRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExisteAlCambiarContrasena() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> cambiarContrasenaUseCase.ejecutar(99L, "nueva"));
    }

    // --- DesactivarUsuarioUseCase ---
    @Test
    void debeDesactivarUsuarioExitosamente() {
        Usuario u = Usuario.builder().id(1L).activo(true).build();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenReturn(u);

        desactivarUsuarioUseCase.ejecutar(1L);

        assertFalse(u.getActivo());
        verify(usuarioRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiUsuarioNoExisteAlDesactivar() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> desactivarUsuarioUseCase.ejecutar(99L));
    }

    // --- ObtenerUsuariosUseCase ---
    @Test
    void debeRetornarListaDeUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(
                Usuario.builder().id(1L).correo("a@g.com").build()
        ));

        List<Usuario> resultado = obtenerUsuariosUseCase.ejecutar();
        assertEquals(1, resultado.size());
    }

    // --- LoginUseCase ---
    @Test
    void debeLanzarExcepcionSiUsuarioInactivo() {
        Usuario u = Usuario.builder().correo("a@g.com").activo(false).build();
        when(usuarioRepository.findByCorreo("a@g.com")).thenReturn(Optional.of(u));

        assertThrows(CredencialesInvalidasException.class,
                () -> loginUseCase.ejecutar("a@g.com"));
    }
}