package com.gimnasio.transmoderno.participantes.domain.usecase;

import com.gimnasio.transmoderno.participantes.domain.exception.ParticipanteYaExisteException;
import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.model.port.ParticipanteRepository;
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
class RegistrarParticipanteUseCaseTest {

    @Mock
    private ParticipanteRepository participanteRepository;

    @InjectMocks
    private RegistrarParticipanteUseCase useCase;

    @Test
    void debeRegistrarParticipanteExitosamente() {
        // Arrange
        Participante participante = Participante.builder()
                .numeroIdentificacion("1000179920")
                .nombreCompleto("Juan Pérez")
                .correoInstitucional("juan@ucundinamarca.edu.co")
                .estamento("ESTUDIANTE")
                .build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.empty());
        when(participanteRepository.save(any()))
                .thenReturn(participante);

        // Act
        Participante resultado = useCase.ejecutar(participante);

        // Assert
        assertNotNull(resultado);
        assertEquals("1000179920", resultado.getNumeroIdentificacion());
        assertEquals("Juan Pérez", resultado.getNombreCompleto());
        verify(participanteRepository, times(1)).save(any());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteYaExiste() {
        // Arrange
        Participante existente = Participante.builder()
                .numeroIdentificacion("1000179920")
                .nombreCompleto("Juan Pérez")
                .build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(existente));

        Participante nuevo = Participante.builder()
                .numeroIdentificacion("1000179920")
                .nombreCompleto("Juan Pérez")
                .build();

        // Act + Assert
        assertThrows(ParticipanteYaExisteException.class,
                () -> useCase.ejecutar(nuevo));

        verify(participanteRepository, never()).save(any());
    }
}