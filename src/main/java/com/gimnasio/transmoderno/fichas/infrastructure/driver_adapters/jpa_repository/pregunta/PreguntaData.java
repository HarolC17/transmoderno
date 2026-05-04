package com.gimnasio.transmoderno.fichas.infrastructure.driver_adapters.jpa_repository.pregunta;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preguntas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreguntaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ruta_id", nullable = false)
    private Long rutaId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;

    @Column(nullable = false)
    private Integer orden;

    @Column(nullable = false, length = 30)
    private String tipo;

    @Column(columnDefinition = "TEXT")
    private String opciones;

    @Column(name = "tip_ficha", nullable = false, length = 10)
    private String tipFicha;

    @Column(nullable = false)
    private Boolean activa;

    @PrePersist
    public void prePersist() {
        if (this.activa == null) this.activa = true;
    }
}