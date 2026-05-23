package com.gimnasio.transmoderno.reportes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReporteCobertura {
    private String programa;
    private long totalMatriculados;   // matriculados en ese programa
    private long totalParticipantes;  // participantes GT en ese programa
    private double porcentaje;        // cobertura de ese programa
    private long totalInstitucion;    // total global de estudiante_ucundinamarca (igual en todas las filas)
}