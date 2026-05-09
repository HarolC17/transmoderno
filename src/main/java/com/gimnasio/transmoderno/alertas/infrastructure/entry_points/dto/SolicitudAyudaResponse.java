package com.gimnasio.transmoderno.alertas.infrastructure.entry_points.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SolicitudAyudaResponse {
    private Long id;
    private Long participanteId;
    private Long atendidaPor;
    private LocalDateTime fechaHora;
    private Boolean atendida;
    private LocalDateTime fechaAtencion;
    // Datos del participante
    private String nombreCompleto;
    private String numeroIdentificacion;
    private String programaAcademico;
    private String correoInstitucional;
    private String telefono;
}