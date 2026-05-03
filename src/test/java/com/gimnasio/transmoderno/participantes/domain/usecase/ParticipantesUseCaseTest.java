package com.gimnasio.transmoderno.participantes.domain.usecase;

import com.gimnasio.transmoderno.participantes.domain.exception.EstudianteNoEncontradoException;
import com.gimnasio.transmoderno.participantes.domain.exception.ParticipanteNoEncontradoException;
import com.gimnasio.transmoderno.participantes.domain.exception.ParticipanteYaExisteException;
import com.gimnasio.transmoderno.participantes.domain.model.EstudianteUcundinamarca;
import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.model.port.EstudianteUcundinamarcaRepository;
import com.gimnasio.transmoderno.participantes.domain.model.port.ParticipanteRepository;
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
class ParticipantesUseCaseTest {

    @Mock private ParticipanteRepository participanteRepository;
    @Mock private EstudianteUcundinamarcaRepository estudianteRepository;

    @InjectMocks private ActualizarParticipanteUseCase actualizarUseCase;
    @InjectMocks private DesactivarParticipanteUseCase desactivarUseCase;
    @InjectMocks private ObtenerParticipantePorIdentificacionUseCase obtenerPorIdUseCase;
    @InjectMocks private ObtenerParticipantesUseCase obtenerParticipantesUseCase;
    @InjectMocks private BuscarEstudianteUcundinamarcaUseCase buscarEstudianteUseCase;

    // --- ActualizarParticipanteUseCase ---
    @Test
    void debeActualizarParticipanteExitosamente() {
        Participante existente = Participante.builder().id(1L)
                .numeroIdentificacion("123").nombreCompleto("A").build();
        Participante actualizado = Participante.builder()
                .numeroIdentificacion("123").nombreCompleto("B").build();

        when(participanteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(participanteRepository.save(any())).thenReturn(existente);

        Participante resultado = actualizarUseCase.ejecutar(1L, actualizado);
        assertNotNull(resultado);
        verify(participanteRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoExisteAlActualizar() {
        when(participanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> actualizarUseCase.ejecutar(99L,
                        Participante.builder().numeroIdentificacion("x").build()));
    }

    @Test
    void debeLanzarExcepcionSiNuevaCedulaYaExiste() {
        Participante existente = Participante.builder().id(1L)
                .numeroIdentificacion("123").build();
        Participante actualizado = Participante.builder()
                .numeroIdentificacion("456").build();
        Participante otro = Participante.builder().id(2L)
                .numeroIdentificacion("456").build();

        when(participanteRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(participanteRepository.findByNumeroIdentificacion("456"))
                .thenReturn(Optional.of(otro));

        assertThrows(ParticipanteYaExisteException.class,
                () -> actualizarUseCase.ejecutar(1L, actualizado));
    }

    // --- DesactivarParticipanteUseCase ---
    @Test
    void debeDesactivarParticipanteExitosamente() {
        Participante p = Participante.builder().id(1L).activo(true).build();
        when(participanteRepository.findById(1L)).thenReturn(Optional.of(p));
        when(participanteRepository.save(any())).thenReturn(p);

        desactivarUseCase.ejecutar(1L);

        assertFalse(p.getActivo());
        verify(participanteRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoExisteAlDesactivar() {
        when(participanteRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> desactivarUseCase.ejecutar(99L));
    }

    // --- ObtenerParticipantePorIdentificacionUseCase ---
    @Test
    void debeRetornarParticipantePorIdentificacion() {
        Participante p = Participante.builder().id(1L).numeroIdentificacion("123").build();
        when(participanteRepository.findByNumeroIdentificacion("123")).thenReturn(Optional.of(p));

        Participante resultado = obtenerPorIdUseCase.ejecutar("123");
        assertEquals("123", resultado.getNumeroIdentificacion());
    }

    @Test
    void debeLanzarExcepcionSiNoEncuentraParticipantePorIdentificacion() {
        when(participanteRepository.findByNumeroIdentificacion("999")).thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> obtenerPorIdUseCase.ejecutar("999"));
    }

    // --- ObtenerParticipantesUseCase ---
    @Test
    void debeRetornarListaDeParticipantes() {
        when(participanteRepository.findAll(0, 10)).thenReturn(List.of(
                Participante.builder().id(1L).build()
        ));
        assertEquals(1, obtenerParticipantesUseCase.ejecutar(0, 10).size());
    }

    @Test
    void debeContarTotalParticipantes() {
        when(participanteRepository.count()).thenReturn(10L);
        assertEquals(10L, obtenerParticipantesUseCase.contarTotal());
    }

    // --- BuscarEstudianteUcundinamarcaUseCase ---
    @Test
    void debeBuscarEstudianteExitosamente() {
        EstudianteUcundinamarca est = EstudianteUcundinamarca.builder()
                .documento("123").primerNombre("Juan").build();
        when(estudianteRepository.findByDocumento("123")).thenReturn(Optional.of(est));

        EstudianteUcundinamarca resultado = buscarEstudianteUseCase.ejecutar("123");
        assertEquals("123", resultado.getDocumento());
    }

    @Test
    void debeLanzarExcepcionSiEstudianteNoExiste() {
        when(estudianteRepository.findByDocumento("999")).thenReturn(Optional.empty());

        assertThrows(EstudianteNoEncontradoException.class,
                () -> buscarEstudianteUseCase.ejecutar("999"));
    }
}