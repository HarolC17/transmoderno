package com.gimnasio.transmoderno.fichas.infrastructure.entry_points.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearPreguntaRequest {

    @NotNull(message = "El id de la ruta es obligatorio")
    private Long rutaId;

    @NotBlank(message = "El texto de la pregunta es obligatorio")
    private String texto;

    @NotNull(message = "El orden es obligatorio")
    private Integer orden;

    @NotBlank(message = "El tipo es obligatorio")
    private String tipo;

    private String opciones;

    @NotBlank(message = "El tipo de ficha es obligatorio")
    private String tipFicha;
}