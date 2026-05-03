package com.gimnasio.transmoderno.asistencia.domain.usecase;

import com.gimnasio.transmoderno.asistencia.domain.exception.AsistenciaYaRegistradaException;
import com.gimnasio.transmoderno.asistencia.domain.exception.ParticipanteNoInscritoException;
import com.gimnasio.transmoderno.asistencia.domain.model.RegistroAsistencia;
import com.gimnasio.transmoderno.asistencia.domain.model.port.RegistroAsistenciaRepository;
import com.gimnasio.transmoderno.inscripciones.domain.model.EstadoInscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.Inscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.port.InscripcionRepository;
import com.gimnasio.transmoderno.participantes.domain.exception.ParticipanteNoEncontradoException;
import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.model.port.ParticipanteRepository;
import com.gimnasio.transmoderno.sesiones.domain.exception.SesionNoActivaException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarAsistenciaUseCaseTest {

    @Mock private RegistroAsistenciaRepository registroAsistenciaRepository;
    @Mock private ParticipanteRepository participanteRepository;
    @Mock private SesionRepository sesionRepository;
    @Mock private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private RegistrarAsistenciaUseCase useCase;

    @Test
    void debeRegistrarAsistenciaExitosamente() {
        // Arrange
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();

        Sesion sesion = Sesion.builder()
                .id(10L).rutaId(1L).build();

        Inscripcion inscripcion = Inscripcion.builder()
                .id(1L).participanteId(1L).rutaId(1L)
                .estado(EstadoInscripcion.ACTIVA).build();

        RegistroAsistencia registro = RegistroAsistencia.builder()
                .id(1L).participanteId(1L).sesionId(10L).build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(sesionRepository.findSesionActiva(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Optional.of(sesion));
        when(inscripcionRepository.findByParticipanteIdAndRutaId(1L, 1L))
                .thenReturn(Optional.of(inscripcion));
        when(registroAsistenciaRepository.findByParticipanteIdAndSesionId(1L, 10L))
                .thenReturn(Optional.empty());
        when(registroAsistenciaRepository.save(any()))
                .thenReturn(registro);

        // Act
        RegistroAsistencia resultado = useCase.ejecutar("1000179920", 1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getParticipanteId());
        assertEquals(10L, resultado.getSesionId());
        verify(registroAsistenciaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoExiste() {
        when(participanteRepository.findByNumeroIdentificacion("9999999"))
                .thenReturn(Optional.empty());

        assertThrows(ParticipanteNoEncontradoException.class,
                () -> useCase.ejecutar("9999999", 1L));

        verify(registroAsistenciaRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiNoHaySesionActiva() {
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(sesionRepository.findSesionActiva(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Optional.empty());

        assertThrows(SesionNoActivaException.class,
                () -> useCase.ejecutar("1000179920", 1L));

        verify(registroAsistenciaRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiParticipanteNoEstaInscrito() {
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();

        Sesion sesion = Sesion.builder()
                .id(10L).rutaId(1L).build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(sesionRepository.findSesionActiva(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Optional.of(sesion));
        when(inscripcionRepository.findByParticipanteIdAndRutaId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(ParticipanteNoInscritoException.class,
                () -> useCase.ejecutar("1000179920", 1L));

        verify(registroAsistenciaRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiAsistenciaYaFueRegistrada() {
        Participante participante = Participante.builder()
                .id(1L).numeroIdentificacion("1000179920").build();

        Sesion sesion = Sesion.builder()
                .id(10L).rutaId(1L).build();

        Inscripcion inscripcion = Inscripcion.builder()
                .id(1L).participanteId(1L).rutaId(1L)
                .estado(EstadoInscripcion.ACTIVA).build();

        RegistroAsistencia yaExiste = RegistroAsistencia.builder()
                .id(5L).participanteId(1L).sesionId(10L).build();

        when(participanteRepository.findByNumeroIdentificacion("1000179920"))
                .thenReturn(Optional.of(participante));
        when(sesionRepository.findSesionActiva(eq(1L), any(LocalDate.class), any(LocalTime.class)))
                .thenReturn(Optional.of(sesion));
        when(inscripcionRepository.findByParticipanteIdAndRutaId(1L, 1L))
                .thenReturn(Optional.of(inscripcion));
        when(registroAsistenciaRepository.findByParticipanteIdAndSesionId(1L, 10L))
                .thenReturn(Optional.of(yaExiste));

        assertThrows(AsistenciaYaRegistradaException.class,
                () -> useCase.ejecutar("1000179920", 1L));

        verify(registroAsistenciaRepository, never()).save(any());
    }
}