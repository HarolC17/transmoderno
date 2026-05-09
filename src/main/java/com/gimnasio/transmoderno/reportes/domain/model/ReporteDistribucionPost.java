// ReporteDistribucionPost.java
package com.gimnasio.transmoderno.reportes.domain.model;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReporteDistribucionPost {
    private Integer orden;
    private String  pregunta;
    private Map<String, Long> distribucion; // {"Mejoró": 3, "Se mantuvo igual": 1, ...}
    private Long total;
}