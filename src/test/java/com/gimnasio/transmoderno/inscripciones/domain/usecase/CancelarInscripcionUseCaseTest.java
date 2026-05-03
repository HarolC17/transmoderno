package com.gimnasio.transmoderno.inscripciones.domain.usecase;

import com.gimnasio.transmoderno.inscripciones.domain.exception.InscripcionNoEncontradaException;
import com.gimnasio.transmoderno.inscripciones.domain.model.EstadoInscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.Inscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.port.InscripcionRepository;
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
class CancelarInscripcionUseCaseTest {

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private CancelarInscripcionUseCase useCase;

    @Test
    void debeCancelarInscripcionExitosamente() {
        // Arrange
        Inscripcion inscripcion = Inscripcion.builder()
                .id(1L)
                .participanteId(1L)
                .rutaId(1L)
                .estado(EstadoInscripcion.ACTIVA)
                .build();

        when(inscripcionRepository.findById(1L))
                .thenReturn(Optional.of(inscripcion));
        when(inscripcionRepository.save(any()))
                .thenReturn(inscripcion);

        // Act
        useCase.ejecutar(1L, "Falta de tiempo");

        // Assert
        verify(inscripcionRepository).save(any());
        assertEquals(EstadoInscripcion.INACTIVA, inscripcion.getEstado());
        assertEquals("Falta de tiempo", inscripcion.getMotivo());
    }

    @Test
    void debeLanzarExcepcionSiInscripcionNoExiste() {
        // Arrange
        when(inscripcionRepository.findById(99L))
                .thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(InscripcionNoEncontradaException.class,
                () -> useCase.ejecutar(99L, "Motivo cualquiera"));

        verify(inscripcionRepository, never()).save(any());
    }
}