package com.gimnasio.transmoderno.alertas.domain.usecase;

import com.gimnasio.transmoderno.alertas.domain.model.SolicitudAyuda;
import com.gimnasio.transmoderno.alertas.domain.model.port.SolicitudAyudaRepository;
import com.gimnasio.transmoderno.participantes.domain.exception.ParticipanteNoEncontradoException;
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
class CrearSolicitudAyudaUseCaseTest {

    @Mock private SolicitudAyudaRepository solicitudAyudaRepository;
    @Mock private ParticipanteRepository participanteRepository;

    @InjectMocks
    private CrearSolicitudAyudaUseCase useCase;

    @Test
    void debeCrearSolicitudExitosamente() {
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();
        SolicitudAyuda solicitud = SolicitudAyuda.builder()
                .id(1L).participanteId(1L).atendida(false).build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(solicitudAyudaRepository.save(any())).thenReturn(solicitud);

        SolicitudAyuda resultado = useCase.ejecutar("1000179920");

        assertNotNull(resultado);
        assertFalse(resultado.getAtendida());
        verify(solicitudAyudaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoExiste() {
        when(participanteRepository.findByNumeroIdentificacion("9999"))
                .thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> useCase.ejecutar("9999"));

        verify(solicitudAyudaRepository, never()).save(any());
    }
}