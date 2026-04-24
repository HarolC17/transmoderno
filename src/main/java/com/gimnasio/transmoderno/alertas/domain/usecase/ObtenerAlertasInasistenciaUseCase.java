package com.gimnasio.transmoderno.alertas.domain.usecase;

import com.gimnasio.transmoderno.alertas.domain.model.AlertaInasistencia;
import com.gimnasio.transmoderno.alertas.domain.model.port.AlertaInasistenciaPort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ObtenerAlertasInasistenciaUseCase {

    private final AlertaInasistenciaPort alertaInasistenciaPort;

    public List<AlertaInasistencia> ejecutar(int page, int size, String nivel, Long rutaId) {
        return alertaInasistenciaPort.obtenerParticipantesSinAsistir(1, page, size, nivel, rutaId);
    }

    public long contarTotal(String nivel, Long rutaId) {
        return alertaInasistenciaPort.contarParticipantesSinAsistir(1, nivel, rutaId);
    }

    public long contarPorNivel(String nivel) {
        return alertaInasistenciaPort.contarParticipantesSinAsistir(1, nivel, null);
    }
}