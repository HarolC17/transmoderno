package com.gimnasio.transmoderno.rutas.domain.usecase;

import com.gimnasio.transmoderno.rutas.domain.exception.RutaNoEncontradaException;
import com.gimnasio.transmoderno.rutas.domain.exception.RutaYaExisteException;
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
class RutasAdicionalesUseCaseTest {

    @Mock private RutaRepository rutaRepository;

    @InjectMocks private ActualizarRutaUseCase actualizarRutaUseCase;
    @InjectMocks private ReactivarRutaUseCase reactivarRutaUseCase;
    @InjectMocks private CrearRutaUseCase crearRutaUseCase;

    // --- CrearRutaUseCase con validación de nombre ---
    @Test
    void debeLanzarExcepcionSiNombreRutaYaExiste() {
        Ruta existente = Ruta.builder().id(1L).nombre("Energía sin Límite").build();
        when(rutaRepository.findByNombre("Energía sin Límite")).thenReturn(Optional.of(existente));

        assertThrows(RutaYaExisteException.class,
                () -> crearRutaUseCase.ejecutar(Ruta.builder().nombre("Energía sin Límite").build()));

        verify(rutaRepository, never()).save(any());
    }

    // --- ActualizarRutaUseCase ---
    @Test
    void debeActualizarRutaExitosamente() {
        Ruta existente = Ruta.builder().id(1L).nombre("Energía sin Límite").descripcion("Desc").build();
        Ruta actualizada = Ruta.builder().nombre("Energía sin Límite").descripcion("Nueva desc").build();

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rutaRepository.save(any())).thenReturn(existente);

        Ruta resultado = actualizarRutaUseCase.ejecutar(1L, actualizada);
        assertNotNull(resultado);
        verify(rutaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiRutaNoExisteAlActualizar() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RutaNoEncontradaException.class,
                () -> actualizarRutaUseCase.ejecutar(99L,
                        Ruta.builder().nombre("X").build()));
    }

    @Test
    void debeLanzarExcepcionSiNuevoNombreYaExisteEnOtraRuta() {
        Ruta existente = Ruta.builder().id(1L).nombre("Energía sin Límite").build();
        Ruta otra = Ruta.builder().id(2L).nombre("Alma Latina").build();

        when(rutaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(rutaRepository.findByNombre("Alma Latina")).thenReturn(Optional.of(otra));

        assertThrows(RutaYaExisteException.class,
                () -> actualizarRutaUseCase.ejecutar(1L,
                        Ruta.builder().nombre("Alma Latina").build()));
    }

    // --- ReactivarRutaUseCase ---
    @Test
    void debeReactivarRutaExitosamente() {
        Ruta ruta = Ruta.builder().id(1L).activa(false).build();
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(rutaRepository.save(any())).thenReturn(ruta);

        reactivarRutaUseCase.ejecutar(1L);

        assertTrue(ruta.getActiva());
        verify(rutaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiRutaNoExisteAlReactivar() {
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RutaNoEncontradaException.class,
                () -> reactivarRutaUseCase.ejecutar(99L));
    }
}