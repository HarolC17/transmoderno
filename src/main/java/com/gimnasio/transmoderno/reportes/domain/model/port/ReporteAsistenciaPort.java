package com.gimnasio.transmoderno.reportes.domain.model.port;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistencia;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistenciaDetalle;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteTendencia;
import java.time.LocalDate;
import java.util.List;

public interface ReporteAsistenciaPort {

    List<ReporteAsistencia> obtenerAsistenciaPorRuta(Long rutaId, String programaAcademico,
                                                     Integer semestre, LocalDate fechaInicio,
                                                     LocalDate fechaFin, String estamento);

    // Ahora acepta programaAcademico para filtrar dentro de la agrupación por programa
    List<ReporteAsistencia> obtenerAsistenciaPorPrograma(Long rutaId, String programaAcademico,
                                                         Integer semestre, LocalDate fechaInicio,
                                                         LocalDate fechaFin, String estamento);

    // Ahora acepta semestre para filtrar dentro de la agrupación por semestre
    List<ReporteAsistencia> obtenerAsistenciaPorSemestre(Long rutaId, String programaAcademico,
                                                         Integer semestre, LocalDate fechaInicio,
                                                         LocalDate fechaFin, String estamento);

    List<ReporteTendencia> obtenerTendenciaSemanal(Long rutaId, LocalDate fechaInicio,
                                                   LocalDate fechaFin);

    List<ReporteAsistenciaDetalle> obtenerDetalleAsistencia(Long rutaId, String programaAcademico,
                                                            Integer semestre, String estamento,
                                                            LocalDate fechaInicio, LocalDate fechaFin);
}