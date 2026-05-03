package com.gimnasio.transmoderno.fichas.domain.usecase;

import com.gimnasio.transmoderno.fichas.domain.exception.FichaPreYaExisteException;
import com.gimnasio.transmoderno.fichas.domain.model.FichaPre;
import com.gimnasio.transmoderno.fichas.domain.model.port.FichaPreRepository;
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
class CrearFichaPreUseCaseTest {

    @Mock private FichaPreRepository fichaPreRepository;
    @Mock private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private CrearFichaPreUseCase useCase;

    @Test
    void debeCrearFichaPreExitosamente() {
        Inscripcion inscripcion = Inscripcion.builder()
                .id(1L).estado(EstadoInscripcion.ACTIVA).build();
        FichaPre fichaPre = FichaPre.builder()
                .id(1L).inscripcionId(1L).build();

        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(fichaPreRepository.findByInscripcionId(1L)).thenReturn(Optional.empty());
        when(fichaPreRepository.save(any())).thenReturn(fichaPre);

        FichaPre resultado = useCase.ejecutar(FichaPre.builder().inscripcionId(1L).build());

        assertNotNull(resultado);
        verify(fichaPreRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiInscripcionNoExiste() {
        when(inscripcionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(InscripcionNoEncontradaException.class,
                () -> useCase.ejecutar(FichaPre.builder().inscripcionId(99L).build()));

        verify(fichaPreRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiFichaPreYaExiste() {
        Inscripcion inscripcion = Inscripcion.builder()
                .id(1L).estado(EstadoInscripcion.ACTIVA).build();
        FichaPre yaExiste = FichaPre.builder().id(5L).inscripcionId(1L).build();

        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion));
        when(fichaPreRepository.findByInscripcionId(1L)).thenReturn(Optional.of(yaExiste));

        assertThrows(FichaPreYaExisteException.class,
                () -> useCase.ejecutar(FichaPre.builder().inscripcionId(1L).build()));

        verify(fichaPreRepository, never()).save(any());
    }
}