package com.gimnasio.transmoderno.reportes.infrastructure.entry_points.dto;

import lombok.*;
import java.util.Map;

@Getter @AllArgsConstructor
public class ReporteDistribucionPostResponse {
    private Integer orden;
    private String  pregunta;
    private Map<String, Long> distribucion;
    private Long    total;
}