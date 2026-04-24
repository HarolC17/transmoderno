package com.gimnasio.transmoderno.alertas.domain.model;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaInasistencia {
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