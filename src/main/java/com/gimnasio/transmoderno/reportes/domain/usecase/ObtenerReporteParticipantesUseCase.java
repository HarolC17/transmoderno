package com.gimnasio.transmoderno.reportes.domain.usecase;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteCobertura;
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

    // semestre agregado como filtro
    public List<ReporteParticipantes> porSemestre(Long rutaId, String programaAcademico,
                                                  Integer semestre, String estamento) {
        return reporteParticipantesPort.obtenerDistribucionPorSemestre(
                rutaId, programaAcademico, semestre, estamento);
    }

    // ahora acepta filtros
    public List<ReporteParticipantes> porRuta(Long rutaId, String programaAcademico,
                                              Integer semestre, String estamento) {
        return reporteParticipantesPort.obtenerParticipantesPorRuta(
                rutaId, programaAcademico, semestre, estamento);
    }

    public List<ReporteParticipantes> porMotivo(Long rutaId) {
        return reporteParticipantesPort.obtenerDistribucionPorMotivo(rutaId);
    }

    public List<ReporteParticipantes> porRecurrencia(Long rutaId) {
        return reporteParticipantesPort.obtenerNivelRecurrencia(rutaId);
    }

    public List<ReporteCobertura> porCobertura() {
        return reporteParticipantesPort.obtenerCoberturaPorPrograma();
    }
}