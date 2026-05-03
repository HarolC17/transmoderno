package com.gimnasio.transmoderno.asistencia.domain.usecase;

import com.gimnasio.transmoderno.asistencia.domain.model.RegistroAsistencia;
import com.gimnasio.transmoderno.asistencia.domain.model.port.RegistroAsistenciaRepository;
import com.gimnasio.transmoderno.participantes.domain.exception.ParticipanteNoEncontradoException;
import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.model.port.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerAsistenciasUseCaseTest {

    @Mock private RegistroAsistenciaRepository registroAsistenciaRepository;
    @Mock private ParticipanteRepository participanteRepository;

    @InjectMocks private ObtenerAsistenciasPorParticipanteUseCase porParticipanteUseCase;
    @InjectMocks private ObtenerAsistenciasPorSesionUseCase porSesionUseCase;

    @Test
    void debeRetornarAsistenciasPorParticipante() {
        Participante p = Participante.builder().id(1L).numeroIdentificacion("123").build();
        List<RegistroAsistencia> registros = List.of(
                RegistroAsistencia.builder().id(1L).participanteId(1L).sesionId(10L).build()
        );
        when(participanteRepository.findByNumeroIdentificacion("123")).thenReturn(Optional.of(p));
        when(registroAsistenciaRepository.findByParticipanteId(1L, 0, 10)).thenReturn(registros);

        List<RegistroAsistencia> resultado = porParticipanteUseCase.ejecutar("123", 0, 10);

        assertEquals(1, resultado.size());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoExisteEnAsistencia() {
        when(participanteRepository.findByNumeroIdentificacion("999")).thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> porParticipanteUseCase.ejecutar("999", 0, 10));
    }

    @Test
    void debeRetornarAsistenciasPorSesion() {
        List<RegistroAsistencia> registros = List.of(
                RegistroAsistencia.builder().id(1L).sesionId(10L).build()
        );
        when(registroAsistenciaRepository.findBySesionId(10L, 0, 10)).thenReturn(registros);

        List<RegistroAsistencia> resultado = porSesionUseCase.ejecutar(10L, 0, 10);

        assertEquals(1, resultado.size());
    }

    @Test
    void debeContarAsistenciasPorSesion() {
        when(registroAsistenciaRepository.countBySesionId(10L)).thenReturn(5L);

        long total = porSesionUseCase.contarTotal(10L);

        assertEquals(5L, total);
    }
}