package com.gimnasio.transmoderno.inscripciones.infrastructure.entry_points.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CancelarInscripcionRequest {
    private String motivo;
}