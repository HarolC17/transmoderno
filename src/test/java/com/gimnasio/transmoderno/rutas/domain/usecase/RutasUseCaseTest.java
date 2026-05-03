package com.gimnasio.transmoderno.rutas.domain.usecase;

import com.gimnasio.transmoderno.rutas.domain.exception.RutaNoEncontradaException;
import com.gimnasio.transmoderno.rutas.domain.model.Ruta;
import com.gimnasio.transmoderno.rutas.domain.model.port.RutaRepository;
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
class RutasUseCaseTest {

    @Mock private RutaRepository rutaRepository;

    // --- CrearRutaUseCase ---

    @InjectMocks
    private CrearRutaUseCase crearRutaUseCase;

    @Test
    void debeCrearRutaExitosamente() {
        Ruta ruta = Ruta.builder().nombre("Energía sin Límite").activa(true).build();
        Ruta guardada = Ruta.builder().id(1L).nombre("Energía sin Límite").activa(true).build();

        when(rutaRepository.save(any())).thenReturn(guardada);

        Ruta resultado = crearRutaUseCase.ejecutar(ruta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(rutaRepository).save(any());
    }

    // --- ObtenerRutasUseCase ---

    @InjectMocks
    private ObtenerRutasUseCase obtenerRutasUseCase;

    @Test
    void debeRetornarListaDeRutas() {
        List<Ruta> rutas = List.of(
                Ruta.builder().id(1L).nombre("Energía sin Límite").activa(true).build(),
                Ruta.builder().id(2L).nombre("Alma Latina").activa(true).build()
        );

        when(rutaRepository.findAll()).thenReturn(rutas);

        List<Ruta> resultado = obtenerRutasUseCase.ejecutar();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(rutaRepository).findAll();
    }

    // --- ObtenerPorIdUseCase ---

    @InjectMocks
    private ObtenerRutaPorIdUseCase obtenerPorIdUseCase;

    @Test
    void debeRetornarRutaPorId() {
        Ruta ruta = Ruta.builder().id(1L).nombre("Energía sin Límite").activa(true).build();
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));

        Ruta resultado = obtenerPorIdUseCase.ejecutar(1L);

        assertNotNull(resultado);
        assertEquals("Energía sin Límite", resultado.getNombre());
    }

    @Test
    void debeLanzarExcepcionSiRutaNoExistePorId() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RutaNoEncontradaException.class,
                () -> obtenerPorIdUseCase.ejecutar(99L));
    }

    // --- DesactivarRutaUseCase ---

    @InjectMocks
    private DesactivarRutaUseCase desactivarRutaUseCase;

    @Test
    void debeDesactivarRutaExitosamente() {
        Ruta ruta = Ruta.builder().id(1L).nombre("Energía sin Límite").activa(true).build();

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any())).thenReturn(ruta);

        desactivarRutaUseCase.ejecutar(1L);

        assertFalse(ruta.getActiva());
        verify(rutaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionAlDesactivarRutaInexistente() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RutaNoEncontradaException.class,
                () -> desactivarRutaUseCase.ejecutar(99L));

        verify(rutaRepository, never()).save(any());
    }
}