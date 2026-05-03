package com.gimnasio.transmoderno.fichas.domain.usecase;

import com.gimnasio.transmoderno.fichas.domain.exception.FichaPostYaExisteException;
import com.gimnasio.transmoderno.fichas.domain.exception.FichaPreNoCompletadaException;
import com.gimnasio.transmoderno.fichas.domain.exception.FichaPreNoEncontradaException;
import com.gimnasio.transmoderno.fichas.domain.model.FichaPost;
import com.gimnasio.transmoderno.fichas.domain.model.FichaPre;
import com.gimnasio.transmoderno.fichas.domain.model.port.FichaPostRepository;
import com.gimnasio.transmoderno.fichas.domain.model.port.FichaPreRepository;
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
class CrearFichaPostUseCaseTest {

    @Mock private FichaPostRepository fichaPostRepository;
    @Mock private FichaPreRepository fichaPreRepository;

    @InjectMocks
    private CrearFichaPostUseCase useCase;

    @Test
    void debeCrearFichaPostExitosamente() {
        FichaPre fichaPre = FichaPre.builder().id(1L).completada(true).build();
        FichaPost fichaPost = FichaPost.builder().id(1L).fichaPreId(1L).build();

        when(fichaPreRepository.findById(1L)).thenReturn(Optional.of(fichaPre));
        when(fichaPostRepository.findByFichaPreId(1L)).thenReturn(Optional.empty());
        when(fichaPostRepository.save(any())).thenReturn(fichaPost);

        FichaPost resultado = useCase.ejecutar(FichaPost.builder().fichaPreId(1L).build());

        assertNotNull(resultado);
        verify(fichaPostRepository).save(any());
    }

    @Test
    void debeLanzarExcepcionSiFichaPreNoExiste() {
        when(fichaPreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(FichaPreNoEncontradaException.class,
                () -> useCase.ejecutar(FichaPost.builder().fichaPreId(99L).build()));

        verify(fichaPostRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiFichaPreNoEstaCompletada() {
        FichaPre fichaPre = FichaPre.builder().id(1L).completada(false).build();

        when(fichaPreRepository.findById(1L)).thenReturn(Optional.of(fichaPre));

        assertThrows(FichaPreNoCompletadaException.class,
                () -> useCase.ejecutar(FichaPost.builder().fichaPreId(1L).build()));

        verify(fichaPostRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiFichaPostYaExiste() {
        FichaPre fichaPre = FichaPre.builder().id(1L).completada(true).build();
        FichaPost yaExiste = FichaPost.builder().id(5L).fichaPreId(1L).build();

        when(fichaPreRepository.findById(1L)).thenReturn(Optional.of(fichaPre));
        when(fichaPostRepository.findByFichaPreId(1L)).thenReturn(Optional.of(yaExiste));

        assertThrows(FichaPostYaExisteException.class,
                () -> useCase.ejecutar(FichaPost.builder().fichaPreId(1L).build()));

        verify(fichaPostRepository, never()).save(any());
    }
}