package com.gimnasio.transmoderno.alertas.domain.model.port;

import com.gimnasio.transmoderno.alertas.domain.model.AlertaInasistencia;
import java.util.List;

public interface AlertaInasistenciaPort {
    List<AlertaInasistencia> obtenerParticipantesSinAsistir(int sesionesMinimas, int page, int size, String nivel, Long rutaId);
    long contarParticipantesSinAsistir(int sesionesMinimas, String nivel, Long rutaId);
}