package com.gimnasio.transmoderno.reportes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ReporteAsistenciaDetalle {
    private String nombreCompleto;
    private String numeroIdentificacion;
    private String programaAcademico;
    private Integer semestre;
    private String estamento;
    private String ruta;
    private String sesion;
    private LocalDate fecha;
}