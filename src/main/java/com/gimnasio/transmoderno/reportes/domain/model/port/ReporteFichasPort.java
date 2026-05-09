package com.gimnasio.transmoderno.reportes.domain.model.port;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteFichas;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteDistribucionPost;
import java.util.List;

public interface ReporteFichasPort {
    List<ReporteFichas> obtenerComparativaPrePost(Long rutaId, String programaAcademico);
    List<ReporteDistribucionPost> obtenerDistribucionPost(Long rutaId, String programaAcademico);
}