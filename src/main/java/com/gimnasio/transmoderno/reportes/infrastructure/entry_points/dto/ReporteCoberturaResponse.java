package com.gimnasio.transmoderno.reportes.infrastructure.entry_points.dto;

public record ReporteCoberturaResponse(
        String programa,
        long totalMatriculados,   // matriculados en ese programa
        long totalParticipantes,  // participantes GT en ese programa
        double porcentaje,        // cobertura de ese programa
        long totalInstitucion     // total global de la institución (13.346)
) {}