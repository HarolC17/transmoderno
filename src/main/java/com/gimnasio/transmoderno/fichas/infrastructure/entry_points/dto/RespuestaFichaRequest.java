package com.gimnasio.transmoderno.fichas.infrastructure.entry_points.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespuestaFichaRequest {

    @NotNull(message = "El id de la pregunta es obligatorio")
    private Long preguntaId;

    @NotBlank(message = "El valor es obligatorio")
    private String valor;
}