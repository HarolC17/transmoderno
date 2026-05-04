package com.gimnasio.transmoderno.fichas.infrastructure.entry_points;

import com.gimnasio.transmoderno.fichas.domain.model.Pregunta;
import com.gimnasio.transmoderno.fichas.domain.usecase.*;
import com.gimnasio.transmoderno.fichas.infrastructure.entry_points.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/preguntas")
@RequiredArgsConstructor
public class PreguntaController {

    private final CrearPreguntaUseCase crearPreguntaUseCase;
    private final ObtenerPreguntasPorRutaUseCase obtenerPreguntasPorRutaUseCase;
    private final ActualizarPreguntaUseCase actualizarPreguntaUseCase;
    private final DesactivarPreguntaUseCase desactivarPreguntaUseCase;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PSICOLOGO')")
    public ResponseEntity<PreguntaResponse> crear(
            @Valid @RequestBody CrearPreguntaRequest request) {
        Pregunta pregunta = Pregunta.builder()
                .rutaId(request.getRutaId())
                .texto(request.getTexto())
                .orden(request.getOrden())
                .tipo(request.getTipo())
                .opciones(request.getOpciones())
                .tipFicha(request.getTipFicha())
                .build();
        Pregunta resultado = crearPreguntaUseCase.ejecutar(pregunta);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(resultado));
    }

    @GetMapping("/ruta/{rutaId}")
    public ResponseEntity<List<PreguntaResponse>> obtenerPorRuta(@PathVariable Long rutaId) {
        List<PreguntaResponse> preguntas = obtenerPreguntasPorRutaUseCase.ejecutar(rutaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(preguntas);
    }

    @GetMapping("/ruta/{rutaId}/ficha/{tipFicha}")
    public ResponseEntity<List<PreguntaResponse>> obtenerPorRutaYFicha(
            @PathVariable Long rutaId,
            @PathVariable String tipFicha) {
        List<PreguntaResponse> preguntas = obtenerPreguntasPorRutaUseCase.ejecutar(rutaId)
                .stream()
                .filter(p -> tipFicha.equalsIgnoreCase(p.getTipFicha()) && Boolean.TRUE.equals(p.getActiva()))
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(preguntas);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PSICOLOGO')")
    public ResponseEntity<PreguntaResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarPreguntaRequest request) {
        Pregunta preguntaActualizada = Pregunta.builder()
                .texto(request.getTexto())
                .orden(request.getOrden())
                .tipo(request.getTipo())
                .opciones(request.getOpciones())
                .build();
        Pregunta resultado = actualizarPreguntaUseCase.ejecutar(id, preguntaActualizada);
        return ResponseEntity.ok(toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PSICOLOGO')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        desactivarPreguntaUseCase.ejecutar(id);
        return ResponseEntity.ok().build();
    }

    private PreguntaResponse toResponse(Pregunta pregunta) {
        return new PreguntaResponse(
                pregunta.getId(),
                pregunta.getRutaId(),
                pregunta.getTexto(),
                pregunta.getOrden(),
                pregunta.getTipo(),
                pregunta.getOpciones(),
                pregunta.getTipFicha(),
                pregunta.getActiva()
        );
    }
}