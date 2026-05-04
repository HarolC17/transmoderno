package com.gimnasio.transmoderno.fichas.domain.usecase;

import com.gimnasio.transmoderno.fichas.domain.exception.FichaPostNoEncontradaException;
import com.gimnasio.transmoderno.fichas.domain.exception.FichaPreNoEncontradaException;
import com.gimnasio.transmoderno.fichas.domain.exception.PreguntaNoEncontradaException;
import com.gimnasio.transmoderno.fichas.domain.model.FichaPost;
import com.gimnasio.transmoderno.fichas.domain.model.FichaPre;
import com.gimnasio.transmoderno.fichas.domain.model.Pregunta;
import com.gimnasio.transmoderno.fichas.domain.model.port.FichaPostRepository;
import com.gimnasio.transmoderno.fichas.domain.model.port.FichaPreRepository;
import com.gimnasio.transmoderno.fichas.domain.model.port.PreguntaRepository;
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
class FichasUseCaseTest {

    @Mock private FichaPreRepository fichaPreRepository;
    @Mock private FichaPostRepository fichaPostRepository;
    @Mock private PreguntaRepository preguntaRepository;

    @InjectMocks private ObtenerFichaPreUseCase obtenerFichaPreUseCase;
    @InjectMocks private ObtenerFichaPostUseCase obtenerFichaPostUseCase;
    @InjectMocks private CrearPreguntaUseCase crearPreguntaUseCase;
    @InjectMocks private ActualizarPreguntaUseCase actualizarPreguntaUseCase;
    @InjectMocks private DesactivarPreguntaUseCase desactivarPreguntaUseCase;
    @InjectMocks private ObtenerPreguntasPorRutaUseCase obtenerPreguntasPorRutaUseCase;

    // --- ObtenerFichaPreUseCase ---
    @Test
    void debeRetornarFichaPreExistente() {
        FichaPre ficha = FichaPre.builder().id(1L).inscripcionId(10L).completada(true).build();
        when(fichaPreRepository.findByInscripcionId(10L)).thenReturn(Optional.of(ficha));

        FichaPre resultado = obtenerFichaPreUseCase.ejecutar(10L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void debeLanzarExcepcionSiFichaPreNoExiste() {
        when(fichaPreRepository.findByInscripcionId(99L)).thenReturn(Optional.empty());

        assertThrows(FichaPreNoEncontradaException.class,
                () -> obtenerFichaPreUseCase.ejecutar(99L));
    }

    // --- ObtenerFichaPostUseCase ---
    @Test
    void debeRetornarFichaPostExistente() {
        FichaPost ficha = FichaPost.builder().id(1L).fichaPreId(5L).completada(true).build();
        when(fichaPostRepository.findByFichaPreId(5L)).thenReturn(Optional.of(ficha));

        FichaPost resultado = obtenerFichaPostUseCase.ejecutar(5L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void debeLanzarExcepcionSiFichaPostNoExiste() {
        when(fichaPostRepository.findByFichaPreId(99L)).thenReturn(Optional.empty());

        assertThrows(FichaPostNoEncontradaException.class,
                () -> obtenerFichaPostUseCase.ejecutar(99L));
    }

    // --- CrearPreguntaUseCase ---
    @Test
    void debeCrearPreguntaExitosamente() {
        Pregunta pregunta = Pregunta.builder()
                .rutaId(1L).texto("¿Cómo te sientes?").tipo("SELECCION")
                .tipFicha("PRE").orden(1).activa(true).build();
        Pregunta guardada = Pregunta.builder().id(1L).rutaId(1L)
                .texto("¿Cómo te sientes?").tipo("SELECCION")
                .tipFicha("PRE").orden(1).activa(true).build();

        when(preguntaRepository.save(any())).thenReturn(guardada);

        Pregunta resultado = crearPreguntaUseCase.ejecutar(pregunta);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(preguntaRepository).save(any());
    }

    // --- ActualizarPreguntaUseCase ---
    @Test
    void debeActualizarPreguntaExitosamente() {
        Pregunta existente = Pregunta.builder().id(1L).texto("Texto original").orden(1).activa(true).build();
        Pregunta actualizada = Pregunta.builder().texto("Texto nuevo").orden(2).build();

        when(preguntaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(preguntaRepository.save(any())).thenReturn(existente);

        Pregunta resultado = actualizarPreguntaUseCase.ejecutar(1L, actualizada);

        assertNotNull(resultado);
        assertEquals("Texto nuevo", existente.getTexto());
        assertEquals(2, existente.getOrden());
        verify(preguntaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiPreguntaNoExisteAlActualizar() {
        when(preguntaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PreguntaNoEncontradaException.class,
                () -> actualizarPreguntaUseCase.ejecutar(99L,
                        Pregunta.builder().texto("X").orden(1).build()));
    }

    // --- DesactivarPreguntaUseCase ---
    @Test
    void debeDesactivarPreguntaExitosamente() {
        Pregunta pregunta = Pregunta.builder().id(1L).activa(true).build();
        when(preguntaRepository.findById(1L)).thenReturn(Optional.of(pregunta));
        when(preguntaRepository.save(any())).thenReturn(pregunta);

        desactivarPreguntaUseCase.ejecutar(1L);

        assertFalse(pregunta.getActiva());
        verify(preguntaRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiPreguntaNoExisteAlDesactivar() {
        when(preguntaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(PreguntaNoEncontradaException.class,
                () -> desactivarPreguntaUseCase.ejecutar(99L));
        verify(preguntaRepository, never()).save(any());
    }

    // --- ObtenerPreguntasPorRutaUseCase ---
    @Test
    void debeRetornarPreguntasPorRuta() {
        List<Pregunta> preguntas = List.of(
                Pregunta.builder().id(1L).rutaId(1L).texto("P1").activa(true).build(),
                Pregunta.builder().id(2L).rutaId(1L).texto("P2").activa(true).build()
        );
        when(preguntaRepository.findByRutaIdAndActivaTrue(1L)).thenReturn(preguntas);

        List<Pregunta> resultado = obtenerPreguntasPorRutaUseCase.ejecutar(1L);

        assertEquals(2, resultado.size());
        verify(preguntaRepository).findByRutaIdAndActivaTrue(1L);
    }

    @Test
    void debeRetornarListaVaciaSiNoHayPreguntas() {
        when(preguntaRepository.findByRutaIdAndActivaTrue(99L)).thenReturn(List.of());

        List<Pregunta> resultado = obtenerPreguntasPorRutaUseCase.ejecutar(99L);

        assertTrue(resultado.isEmpty());
    }
}