package com.gimnasio.transmoderno.rutas.infrastructure.driver_adapters.jpa_repository;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rutas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RutaData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private Boolean activa;

    @Column(name = "post_habilitado", nullable = false)
    private Boolean postHabilitado;

    @PrePersist
    public void prePersist() {
        if (this.activa == null) this.activa = true;
        if (this.postHabilitado == null) this.postHabilitado = false;
    }
}