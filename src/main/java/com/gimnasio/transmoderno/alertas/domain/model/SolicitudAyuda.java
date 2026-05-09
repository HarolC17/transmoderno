package com.gimnasio.transmoderno.alertas.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAyuda {
    private Long id;
    private Long participanteId;
    private Long atendidaPor;
    private LocalDateTime fechaHora;
    private Boolean atendida;
    private LocalDateTime fechaAtencion;
    // Datos enriquecidos del participante
    private String nombreCompleto;
    private String numeroIdentificacion;
    private String programaAcademico;
    private String correoInstitucional;
    private String telefono;
}