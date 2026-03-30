package com.gimnasio.transmoderno.participantes.infrastructure.entry_points;

import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.usecase.*;
import com.gimnasio.transmoderno.participantes.infrastructure.entry_points.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/participantes")
@RequiredArgsConstructor
public class ParticipanteController {

    private final RegistrarParticipanteUseCase registrarParticipanteUseCase;
    private final ObtenerParticipantesUseCase obtenerParticipantesUseCase;
    private final ObtenerParticipantePorIdentificacionUseCase obtenerParticipantePorIdentificacionUseCase;
    private final ActualizarParticipanteUseCase actualizarParticipanteUseCase;
    private final DesactivarParticipanteUseCase desactivarParticipanteUseCase;

    @PostMapping
    public ResponseEntity<ParticipanteResponse> registrar(
            @Valid @RequestBody RegistrarParticipanteRequest request) {

        Participante participante = Participante.builder()
                .numeroIdentificacion(request.getNumeroIdentificacion())
                .nombreCompleto(request.getNombreCompleto())
                .correoInstitucional(request.getCorreoInstitucional())
                .programaAcademico(request.getProgramaAcademico())
                .semestre(request.getSemestre())
                .build();

        Participante resultado = registrarParticipanteUseCase.ejecutar(participante);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(resultado));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<List<ParticipanteResponse>> obtenerTodos() {
        List<ParticipanteResponse> participantes = obtenerParticipantesUseCase.ejecutar()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(participantes);
    }

    @GetMapping("/identificacion/{numero}")
    public ResponseEntity<ParticipanteResponse> obtenerPorIdentificacion(
            @PathVariable String numero) {
        Participante participante = obtenerParticipantePorIdentificacionUseCase.ejecutar(numero);
        return ResponseEntity.ok(toResponse(participante));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ParticipanteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarParticipanteRequest request) {

        Participante participanteActualizado = Participante.builder()
                .numeroIdentificacion(request.getNumeroIdentificacion())
                .nombreCompleto(request.getNombreCompleto())
                .correoInstitucional(request.getCorreoInstitucional())
                .programaAcademico(request.getProgramaAcademico())
                .semestre(request.getSemestre())
                .build();

        Participante resultado = actualizarParticipanteUseCase.ejecutar(id, participanteActualizado);
        return ResponseEntity.ok(toResponse(resultado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        desactivarParticipanteUseCase.ejecutar(id);
        return ResponseEntity.ok().build();
    }

    private ParticipanteResponse toResponse(Participante participante) {
        return new ParticipanteResponse(
                participante.getId(),
                participante.getNumeroIdentificacion(),
                participante.getNombreCompleto(),
                participante.getCorreoInstitucional(),
                participante.getProgramaAcademico(),
                participante.getSemestre(),
                participante.getFechaRegistro(),
                participante.getActivo()
        );
    }
}