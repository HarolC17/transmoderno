package com.gimnasio.transmoderno.inscripciones.domain.usecase;

import com.gimnasio.transmoderno.inscripciones.domain.exception.InscripcionNoEncontradaException;
import com.gimnasio.transmoderno.inscripciones.domain.model.EstadoInscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.Inscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.port.InscripcionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InscripcionesUseCaseTest {

    @Mock private InscripcionRepository inscripcionRepository;
    @Mock private ParticipanteRepository participanteRepository;

    @InjectMocks private FinalizarInscripcionUseCase finalizarUseCase;
    @InjectMocks private CerrarSemestreUseCase cerrarSemestreUseCase;
    @InjectMocks private DesactivarInscripcionesPorParticipanteUseCase desactivarPorParticipanteUseCase;
    @InjectMocks private ObtenerInscripcionesUseCase obtenerInscripcionesUseCase;
    @InjectMocks private ObtenerInscripcionesPorParticipanteUseCase obtenerPorParticipanteUseCase;

    // --- FinalizarInscripcionUseCase ---
    @Test
    void debeFinalizarInscripcionExitosamente() {
        Inscripcion i = Inscripcion.builder().id(1L).estado(EstadoInscripcion.ACTIVA).build();
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(i));
        when(inscripcionRepository.save(any())).thenReturn(i);

        finalizarUseCase.ejecutar(1L);

        assertEquals(EstadoInscripcion.FINALIZADA, i.getEstado());
        verify(inscripcionRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiInscripcionNoExisteAlFinalizar() {
        when(inscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InscripcionNoEncontradaException.class,
                () -> finalizarUseCase.ejecutar(99L));
    }

    // --- CerrarSemestreUseCase ---
    @Test
    void debeCerrarSemestreFinalizandoInscripcionesActivas() {
        List<Inscripcion> inscripciones = List.of(
                Inscripcion.builder().id(1L).estado(EstadoInscripcion.ACTIVA).build(),
                Inscripcion.builder().id(2L).estado(EstadoInscripcion.INACTIVA).build()
        );
        when(inscripcionRepository.findAll(0, Integer.MAX_VALUE)).thenReturn(inscripciones);
        when(inscripcionRepository.save(any())).thenReturn(inscripciones.get(0));

        int resultado = cerrarSemestreUseCase.ejecutar();

        assertEquals(1, resultado);
        verify(inscripcionRepository, times(1)).save(any());
    }

    // --- DesactivarInscripcionesPorParticipanteUseCase ---
    @Test
    void debeDesactivarInscripcionesActivasDelParticipante() {
        List<Inscripcion> inscripciones = List.of(
                Inscripcion.builder().id(1L).participanteId(1L).estado(EstadoInscripcion.ACTIVA).build()
        );
        when(inscripcionRepository.findByParticipanteId(1L)).thenReturn(inscripciones);
        when(inscripcionRepository.save(any())).thenReturn(inscripciones.get(0));

        desactivarPorParticipanteUseCase.ejecutar(1L);

        assertEquals(EstadoInscripcion.INACTIVA, inscripciones.get(0).getEstado());
        verify(inscripcionRepository).save(any());
    }

    // --- ObtenerInscripcionesUseCase ---
    @Test
    void debeRetornarInscripciones() {
        when(inscripcionRepository.findAll(0, 10)).thenReturn(List.of(
                Inscripcion.builder().id(1L).build()
        ));
        List<Inscripcion> resultado = obtenerInscripcionesUseCase.ejecutar(0, 10);
        assertEquals(1, resultado.size());
    }

    @Test
    void debeContarTotalInscripciones() {
        when(inscripcionRepository.count()).thenReturn(5L);
        assertEquals(5L, obtenerInscripcionesUseCase.contarTotal());
    }

    // --- ObtenerInscripcionesPorParticipanteUseCase ---
    @Test
    void debeRetornarInscripcionesPorParticipante() {
        Participante p = Participante.builder().id(1L).numeroIdentificacion("123").build();
        when(participanteRepository.findByNumeroIdentificacion("123")).thenReturn(Optional.of(p));
        when(inscripcionRepository.findByParticipanteId(1L)).thenReturn(List.of(
                Inscripcion.builder().id(1L).participanteId(1L).build()
        ));

        List<Inscripcion> resultado = obtenerPorParticipanteUseCase.ejecutar("123");
        assertEquals(1, resultado.size());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoExisteEnInscripciones() {
        when(participanteRepository.findByNumeroIdentificacion("999")).thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> obtenerPorParticipanteUseCase.ejecutar("999"));
    }
}