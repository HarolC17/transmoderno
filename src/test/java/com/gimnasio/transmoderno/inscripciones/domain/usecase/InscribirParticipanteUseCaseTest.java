package com.gimnasio.transmoderno.inscripciones.domain.usecase;

import com.gimnasio.transmoderno.inscripciones.domain.exception.ParticipanteYaInscritoException;
import com.gimnasio.transmoderno.inscripciones.domain.model.EstadoInscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.Inscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.port.InscripcionRepository;
import com.gimnasio.transmoderno.participantes.domain.exception.ParticipanteNoEncontradoException;
import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.model.port.ParticipanteRepository;
import com.gimnasio.transmoderno.rutas.domain.exception.RutaNoEncontradaException;
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
class InscribirParticipanteUseCaseTest {

    @Mock private InscripcionRepository inscripcionRepository;
    @Mock private ParticipanteRepository participanteRepository;
    @Mock private RutaRepository rutaRepository;

    @InjectMocks
    private InscribirParticipanteUseCase useCase;

    @Test
    void debeInscribirParticipanteExitosamente() {
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();
        Ruta ruta = Ruta.builder().id(1L).activa(true).build();
        Inscripcion inscripcion = Inscripcion.builder()
                .id(1L).participanteId(1L).rutaId(1L)
                .estado(EstadoInscripcion.ACTIVA).build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(inscripcionRepository.findByParticipanteIdAndRutaId(1L, 1L))
                .thenReturn(Optional.empty());
        when(inscripcionRepository.save(any())).thenReturn(inscripcion);

        Inscripcion resultado = useCase.ejecutar("1000179920", 1L);

        assertNotNull(resultado);
        assertEquals(EstadoInscripcion.ACTIVA, resultado.getEstado());
        verify(inscripcionRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoExiste() {
        when(participanteRepository.findByNumeroIdentificacion("9999"))
                .thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> useCase.ejecutar("9999", 1L));

        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiRutaNoExiste() {
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(rutaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RutaNoEncontradaException.class,
                () -> useCase.ejecutar("1000179920", 99L));

        verify(inscripcionRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiYaEstaInscritoActivo() {
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();
        Ruta ruta = Ruta.builder().id(1L).activa(true).build();
        Inscripcion activa = Inscripcion.builder()
                .id(1L).participanteId(1L).rutaId(1L)
                .estado(EstadoInscripcion.ACTIVA).build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(rutaRepository.findById(1L)).thenReturn(Optional.of(ruta));
        when(inscripcionRepository.findByParticipanteIdAndRutaId(1L, 1L))
                .thenReturn(Optional.of(activa));

        assertThrows(ParticipanteYaInscritoException.class,
                () -> useCase.ejecutar("1000179920", 1L));

        verify(inscripcionRepository, never()).save(any());
    }
}