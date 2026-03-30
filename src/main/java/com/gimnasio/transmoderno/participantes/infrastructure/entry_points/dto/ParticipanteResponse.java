package com.gimnasio.transmoderno.participantes.infrastructure.entry_points.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ParticipanteResponse {
    private Long id;
    private String numeroIdentificacion;
    private String nombreCompleto;
    private String correoInstitucional;
    private String programaAcademico;
    private Integer semestre;
    private LocalDateTime fechaRegistro;
    private Boolean activo;
}