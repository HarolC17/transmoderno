package com.gimnasio.transmoderno.rutas.domain.usecase;

import com.gimnasio.transmoderno.rutas.domain.exception.RutaNoEncontradaException;
import com.gimnasio.transmoderno.rutas.domain.model.Ruta;
import com.gimnasio.transmoderno.rutas.domain.model.port.RutaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabilitarPostUseCaseTest {

    @Mock private RutaRepository rutaRepository;
    @InjectMocks private HabilitarPostUseCase habilitarPostUseCase;

    @Test
    void debeHabilitarPostExitosamente() {
        Ruta ruta = Ruta.builder().id(1L).nombre("Energía sin Límite").postHabilitado(false).build();
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any())).thenReturn(ruta);

        habilitarPostUseCase.ejecutar(1L, true);

        assertTrue(ruta.getPostHabilitado());
        verify(rutaRepository).save(any());
    }

    @Test
    void debeDeshabilitarPostExitosamente() {
        Ruta ruta = Ruta.builder().id(1L).nombre("Energía sin Límite").postHabilitado(true).build();
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any())).thenReturn(ruta);

        habilitarPostUseCase.ejecutar(1L, false);

        assertFalse(ruta.getPostHabilitado());
        verify(rutaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiRutaNoExiste() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RutaNoEncontradaException.class,
                () -> habilitarPostUseCase.ejecutar(99L, true));
        verify(rutaRepository, never()).save(any());
    }
}