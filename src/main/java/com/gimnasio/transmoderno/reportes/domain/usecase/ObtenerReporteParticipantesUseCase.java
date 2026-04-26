package com.gimnasio.transmoderno.reportes.domain.usecase;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteParticipantes;
import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteParticipantesPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ObtenerReporteParticipantesUseCase {

    private final ReporteParticipantesPort reporteParticipantesPort;

    public List<ReporteParticipantes> porPrograma(Long rutaId, Integer semestre,
                                                  String programaAcademico, String estamento) {
        return reporteParticipantesPort.obtenerDistribucionPorPrograma(
                rutaId, semestre, programaAcademico, estamento);
    }

    public List<ReporteParticipantes> porSemestre(Long rutaId, String programaAcademico,
                                                  String estamento) {
        return reporteParticipantesPort.obtenerDistribucionPorSemestre(
                rutaId, programaAcademico, estamento);
    }

    public List<ReporteParticipantes> porRuta() {
        return reporteParticipantesPort.obtenerParticipantesPorRuta();
    }

    public List<ReporteParticipantes> porMotivo(Long rutaId) {
        return reporteParticipantesPort.obtenerDistribucionPorMotivo(rutaId);
    }
}