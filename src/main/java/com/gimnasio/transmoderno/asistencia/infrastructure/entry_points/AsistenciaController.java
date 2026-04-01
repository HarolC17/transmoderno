package com.gimnasio.transmoderno.asistencia.infrastructure.entry_points;

import com.gimnasio.transmoderno.asistencia.domain.model.RegistroAsistencia;
import com.gimnasio.transmoderno.asistencia.domain.usecase.*;
import com.gimnasio.transmoderno.asistencia.infrastructure.entry_points.dto.*;
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
@RequestMapping("/api/asistencia")
@RequiredArgsConstructor
public class AsistenciaController {

    private final RegistrarAsistenciaUseCase registrarAsistenciaUseCase;
    private final ObtenerAsistenciasPorSesionUseCase obtenerAsistenciasPorSesionUseCase;
    private final ObtenerAsistenciasPorParticipanteUseCase obtenerAsistenciasPorParticipanteUseCase;

    @PostMapping
    public ResponseEntity<RegistroAsistenciaResponse> registrar(
            @Valid @RequestBody RegistrarAsistenciaRequest request) {
        RegistroAsistencia registro = registrarAsistenciaUseCase.ejecutar(
                request.getNumeroIdentificacion(),
                request.getRutaId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(registro));
    }

    @GetMapping("/sesion/{sesionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<PaginaResponse<RegistroAsistenciaResponse>> obtenerPorSesion(
            @PathVariable Long sesionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<RegistroAsistencia> registros = obtenerAsistenciasPorSesionUseCase
                .ejecutar(sesionId, page, size);
        long total = obtenerAsistenciasPorSesionUseCase.contarTotal(sesionId);

        PaginaResponse<RegistroAsistenciaResponse> respuesta = new PaginaResponse<>(
                registros.stream().map(this::toResponse).collect(Collectors.toList()),
                page,
                (int) Math.ceil((double) total / size),
                total,
                size
        );

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/participante/{numeroIdentificacion}")
    public ResponseEntity<PaginaResponse<RegistroAsistenciaResponse>> obtenerPorParticipante(
            @PathVariable String numeroIdentificacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<RegistroAsistencia> registros = obtenerAsistenciasPorParticipanteUseCase
                .ejecutar(numeroIdentificacion, page, size);
        long total = obtenerAsistenciasPorParticipanteUseCase.contarTotal(numeroIdentificacion);

        PaginaResponse<RegistroAsistenciaResponse> respuesta = new PaginaResponse<>(
                registros.stream().map(this::toResponse).collect(Collectors.toList()),
                page,
                (int) Math.ceil((double) total / size),
                total,
                size
        );

        return ResponseEntity.ok(respuesta);
    }

    private RegistroAsistenciaResponse toResponse(RegistroAsistencia registro) {
        return new RegistroAsistenciaResponse(
                registro.getId(),
                registro.getParticipanteId(),
                registro.getSesionId(),
                registro.getFechaHoraRegistro()
        );
    }
}