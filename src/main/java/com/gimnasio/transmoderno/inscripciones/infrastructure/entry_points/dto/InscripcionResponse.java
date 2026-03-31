package com.gimnasio.transmoderno.inscripciones.infrastructure.entry_points.dto;

import com.gimnasio.transmoderno.inscripciones.domain.model.EstadoInscripcion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InscripcionResponse {
    private Long id;
    private Long participanteId;
    private Long rutaId;
    private LocalDateTime fechaInscripcion;
    private EstadoInscripcion estado;
}