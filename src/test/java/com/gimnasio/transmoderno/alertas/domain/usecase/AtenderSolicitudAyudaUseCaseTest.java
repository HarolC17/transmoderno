package com.gimnasio.transmoderno.alertas.domain.usecase;

import com.gimnasio.transmoderno.alertas.domain.exception.SolicitudAyudaNoEncontradaException;
import com.gimnasio.transmoderno.alertas.domain.model.SolicitudAyuda;
import com.gimnasio.transmoderno.alertas.domain.model.port.SolicitudAyudaRepository;
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
class AtenderSolicitudAyudaUseCaseTest {

    @Mock
    private SolicitudAyudaRepository solicitudAyudaRepository;

    @InjectMocks
    private AtenderSolicitudAyudaUseCase useCase;

    @Test
    void debeAtenderSolicitudExitosamente() {
        // Arrange
        SolicitudAyuda solicitud = SolicitudAyuda.builder()
                .id(1L)
                .participanteId(1L)
                .atendida(false)
                .build();

        when(solicitudAyudaRepository.findById(1L))
                .thenReturn(Optional.of(solicitud));
        when(solicitudAyudaRepository.save(any()))
                .thenReturn(solicitud);

        // Act
        SolicitudAyuda resultado = useCase.ejecutar(1L, 2L);

        // Assert
        assertNotNull(resultado);
        assertTrue(solicitud.getAtendida());
        assertEquals(2L, solicitud.getAtendidaPor());
        assertNotNull(solicitud.getFechaAtencion());
        verify(solicitudAyudaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiSolicitudNoExiste() {
        // Arrange
        when(solicitudAyudaRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(SolicitudAyudaNoEncontradaException.class,
                () -> useCase.ejecutar(99L, 1L));

        verify(solicitudAyudaRepository, never()).save(any());
    }
}