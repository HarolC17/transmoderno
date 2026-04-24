package com.gimnasio.transmoderno.alertas.infrastructure.entry_points.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AlertaInasistenciaResponse {
    private Long participanteId;
    private String numeroIdentificacion;
    private String nombreCompleto;
    private String correoInstitucional;
    private String telefono;
    private Long rutaId;
    private String nombreRuta;
    private LocalDateTime ultimaAsistencia;
    private Long sesionesSinAsistir;
    private String nivelRiesgo;
}