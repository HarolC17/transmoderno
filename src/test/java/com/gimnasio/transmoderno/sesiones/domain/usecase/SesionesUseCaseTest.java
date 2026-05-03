package com.gimnasio.transmoderno.sesiones.domain.usecase;

import com.gimnasio.transmoderno.sesiones.domain.exception.SesionNoActivaException;
import com.gimnasio.transmoderno.sesiones.domain.exception.SesionNoEncontradaException;
import com.gimnasio.transmoderno.sesiones.domain.model.Sesion;
import com.gimnasio.transmoderno.sesiones.domain.model.port.SesionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesionesUseCaseTest {

    @Mock private SesionRepository sesionRepository;

    @InjectMocks private EliminarSesionUseCase eliminarSesionUseCase;
    @InjectMocks private ObtenerSesionActivaUseCase obtenerSesionActivaUseCase;
    @InjectMocks private ObtenerSesionesPorRutaUseCase obtenerPorRutaUseCase;
    @InjectMocks private ObtenerSesionPorIdUseCase obtenerPorIdUseCase;

    // --- EliminarSesionUseCase ---
    @Test
    void debeEliminarSesionExitosamente() {
        Sesion s = Sesion.builder().id(1L).build();
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(s));

        eliminarSesionUseCase.ejecutar(1L);

        verify(sesionRepository).deleteById(1L);
    }

    @Test
    void debeLanzarExcepcionSiSesionNoExisteAlEliminar() {
        when(sesionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SesionNoEncontradaException.class,
                () -> eliminarSesionUseCase.ejecutar(99L));

        verify(sesionRepository, never()).deleteById(any());
    }

    // --- ObtenerSesionActivaUseCase ---
    @Test
    void debeRetornarSesionActiva() {
        Sesion s = Sesion.builder().id(10L).rutaId(1L).build();
        when(sesionRepository.findSesionActiva(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Optional.of(s));

        Sesion resultado = obtenerSesionActivaUseCase.ejecutar(1L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
    }

    @Test
    void debeLanzarExcepcionSiNoHaySesionActiva() {
        when(sesionRepository.findSesionActiva(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(SesionNoActivaException.class,
                () -> obtenerSesionActivaUseCase.ejecutar(1L));
    }

    // --- ObtenerSesionesPorRutaUseCase ---
    @Test
    void debeRetornarSesionesPorRuta() {
        when(sesionRepository.findByRutaId(1L, 0, 10)).thenReturn(List.of(
                Sesion.builder().id(1L).rutaId(1L).build()
        ));

        List<Sesion> resultado = obtenerPorRutaUseCase.ejecutar(1L, 0, 10);
        assertEquals(1, resultado.size());
    }

    @Test
    void debeContarSesionesPorRuta() {
        when(sesionRepository.countByRutaId(1L)).thenReturn(3L);
        assertEquals(3L, obtenerPorRutaUseCase.contarTotal(1L));
    }

    // --- ObtenerSesionPorIdUseCase ---
    @Test
    void debeRetornarSesionPorId() {
        Sesion s = Sesion.builder().id(1L).nombre("Sesión 1").build();
        when(sesionRepository.findById(1L)).thenReturn(Optional.of(s));

        Sesion resultado = obtenerPorIdUseCase.ejecutar(1L);
        assertEquals("Sesión 1", resultado.getNombre());
    }

    @Test
    void debeLanzarExcepcionSiSesionNoExistePorId() {
        when(sesionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(SesionNoEncontradaException.class,
                () -> obtenerPorIdUseCase.ejecutar(99L));
    }
}