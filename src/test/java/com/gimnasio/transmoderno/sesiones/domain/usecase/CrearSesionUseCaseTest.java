package com.gimnasio.transmoderno.sesiones.domain.usecase;

import com.gimnasio.transmoderno.rutas.domain.exception.RutaNoEncontradaException;
import com.gimnasio.transmoderno.rutas.domain.model.Ruta;
import com.gimnasio.transmoderno.rutas.domain.model.port.RutaRepository;
import com.gimnasio.transmoderno.sesiones.domain.exception.FechaSesionInvalidaException;
import com.gimnasio.transmoderno.sesiones.domain.model.Sesion;
import com.gimnasio.transmoderno.sesiones.domain.model.port.SesionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearSesionUseCaseTest {

    @Mock private SesionRepository sesionRepository;
    @Mock private RutaRepository rutaRepository;

    @InjectMocks
    private CrearSesionUseCase useCase;

    @Test
    void debeCrearSesionExitosamente() {
        Sesion sesion = Sesion.builder()
                .rutaId(1L).nombre("Sesión 1")
                .fecha(LocalDate.now().plusDays(1))
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(9, 0))
                .build();
        Sesion guardada = Sesion.builder()
                .id(1L).rutaId(1L).nombre("Sesión 1")
                .fecha(LocalDate.now().plusDays(1))
                .build();

        when(sesionRepository.save(any())).thenReturn(guardada);

        Sesion resultado = useCase.ejecutar(sesion);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(sesionRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiFechaEsPasada() {
        Sesion sesion = Sesion.builder()
                .rutaId(1L).nombre("Sesión X")
                .fecha(LocalDate.now().minusDays(1))
                .horaInicio(LocalTime.of(8, 0))
                .horaFin(LocalTime.of(9, 0))
                .build();

        assertThrows(FechaSesionInvalidaException.class,
                () -> useCase.ejecutar(sesion));

        verify(sesionRepository, never()).save(any());
    }
}