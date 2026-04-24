package com.gimnasio.transmoderno.reportes.infrastructure.entry_points.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReporteAsistenciaDetalleResponse {
    private String nombreCompleto;
    private String numeroIdentificacion;
    private String programaAcademico;
    private Integer semestre;
    private String estamento;
    private String ruta;
    private String sesion;
    private LocalDate fecha;
}