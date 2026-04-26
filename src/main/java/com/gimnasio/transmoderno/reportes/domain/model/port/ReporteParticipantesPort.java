package com.gimnasio.transmoderno.reportes.domain.model.port;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteParticipantes;
import java.util.List;

public interface ReporteParticipantesPort {
    List<ReporteParticipantes> obtenerDistribucionPorPrograma(Long rutaId, Integer semestre,
                                                              String programaAcademico, String estamento);
    List<ReporteParticipantes> obtenerDistribucionPorSemestre(Long rutaId, String programaAcademico,
                                                              String estamento);
    List<ReporteParticipantes> obtenerParticipantesPorRuta();

    List<ReporteParticipantes> obtenerDistribucionPorMotivo(Long rutaId);
}