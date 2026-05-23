package com.gimnasio.transmoderno.participantes.infrastructure.entry_points.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarParticipanteRequest {

    @NotBlank(message = "El número de identificación es obligatorio")
    @Pattern(
            regexp = "^[0-9]+$",
            message = "La identificación solo puede contener dígitos"
    )
    private String numeroIdentificacion;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Pattern(
            regexp = "^[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ\\s]+$",
            message = "El nombre solo puede contener letras y espacios, sin números ni caracteres especiales"
    )
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El correo institucional es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String correoInstitucional;

    @NotBlank(message = "El programa académico es obligatorio")
    @Size(max = 150, message = "El programa no puede superar los 150 caracteres")
    private String programaAcademico;

    @Min(value = 1, message = "El semestre mínimo es 1")
    @Max(value = 10, message = "El semestre máximo es 10")
    private Integer semestre;

    private String tipoDocumento;
    private String sede;

    @Pattern(
            regexp = "^[0-9]{7,15}$",
            message = "El teléfono debe contener entre 7 y 15 dígitos"
    )
    private String telefono;

    private String estamento;
}