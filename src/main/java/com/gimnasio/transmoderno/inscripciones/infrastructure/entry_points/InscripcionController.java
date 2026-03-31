package com.gimnasio.transmoderno.inscripciones.infrastructure.entry_points;

import com.gimnasio.transmoderno.inscripciones.domain.model.Inscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.usecase.*;
import com.gimnasio.transmoderno.inscripciones.infrastructure.entry_points.dto.*;
import com.gimnasio.transmoderno.shared.dto.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscribirParticipanteUseCase inscribirParticipanteUseCase;
    private final ObtenerInscripcionesUseCase obtenerInscripcionesUseCase;
    private final ObtenerInscripcionesPorParticipanteUseCase obtenerInscripcionesPorParticipanteUseCase;
    private final FinalizarInscripcionUseCase finalizarInscripcionUseCase;

    @PostMapping
    public ResponseEntity<InscripcionResponse> inscribir(
            @Valid @RequestBody InscribirParticipanteRequest request) {
        Inscripcion inscripcion = inscribirParticipanteUseCase.ejecutar(
                request.getNumeroIdentificacion(),
                request.getRutaId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(inscripcion));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<PaginaResponse<InscripcionResponse>> obtenerTodas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<Inscripcion> inscripciones = obtenerInscripcionesUseCase.ejecutar(page, size);
        long total = obtenerInscripcionesUseCase.contarTotal();

        PaginaResponse<InscripcionResponse> respuesta = new PaginaResponse<>(
                inscripciones.stream().map(this::toResponse).collect(Collectors.toList()),
                page,
                (int) Math.ceil((double) total / size),
                total,
                size
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/participante/{numeroIdentificacion}")
    public ResponseEntity<List<InscripcionResponse>> obtenerPorParticipante(
            @PathVariable String numeroIdentificacion) {
        List<InscripcionResponse> inscripciones = obtenerInscripcionesPorParticipanteUseCase
                .ejecutar(numeroIdentificacion)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inscripciones);
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> finalizar(@PathVariable Long id) {
        finalizarInscripcionUseCase.ejecutar(id);
        return ResponseEntity.ok().build();
    }

    private InscripcionResponse toResponse(Inscripcion inscripcion) {
        return new InscripcionResponse(
                inscripcion.getId(),
                inscripcion.getParticipanteId(),
                inscripcion.getRutaId(),
                inscripcion.getFechaInscripcion(),
                inscripcion.getEstado()
        );
    }
}