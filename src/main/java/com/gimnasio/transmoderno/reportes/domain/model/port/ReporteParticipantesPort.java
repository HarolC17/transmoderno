package com.gimnasio.transmoderno.reportes.domain.model.port;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteCobertura;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteParticipantes;
import java.util.List;

public interface ReporteParticipantesPort {

    // programaAcademico ya estaba, semestre ya estaba ✓
    List<ReporteParticipantes> obtenerDistribucionPorPrograma(Long rutaId, Integer semestre,
                                                              String programaAcademico, String estamento);

    // agregamos semestre como filtro adicional
    List<ReporteParticipantes> obtenerDistribucionPorSemestre(Long rutaId, String programaAcademico,
                                                              Integer semestre, String estamento);

    // agregamos todos los filtros — antes no tenía ninguno
    List<ReporteParticipantes> obtenerParticipantesPorRuta(Long rutaId, String programaAcademico,
                                                           Integer semestre, String estamento);

    List<ReporteParticipantes> obtenerDistribucionPorMotivo(Long rutaId);

    List<ReporteParticipantes> obtenerNivelRecurrencia(Long rutaId);

    List<ReporteCobertura>     obtenerCoberturaPorPrograma();
}