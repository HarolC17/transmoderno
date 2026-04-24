package com.gimnasio.transmoderno.reportes.domain.usecase;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistencia;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistenciaDetalle;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteTendencia;
import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteAsistenciaPort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ObtenerReporteAsistenciaUseCase {

    private final ReporteAsistenciaPort reporteAsistenciaPort;

    public List<ReporteAsistencia> porRuta(Long rutaId, String programaAcademico,
                                           Integer semestre, LocalDate fechaInicio,
                                           LocalDate fechaFin, String estamento) {
        return reporteAsistenciaPort.obtenerAsistenciaPorRuta(
                rutaId, programaAcademico, semestre, fechaInicio, fechaFin, estamento);
    }

    public List<ReporteAsistencia> porPrograma(Long rutaId, Integer semestre,
                                               LocalDate fechaInicio, LocalDate fechaFin,
                                               String estamento) {
        return reporteAsistenciaPort.obtenerAsistenciaPorPrograma(
                rutaId, semestre, fechaInicio, fechaFin, estamento);
    }

    public List<ReporteAsistencia> porSemestre(Long rutaId, String programaAcademico,
                                               LocalDate fechaInicio, LocalDate fechaFin,
                                               String estamento) {
        return reporteAsistenciaPort.obtenerAsistenciaPorSemestre(
                rutaId, programaAcademico, fechaInicio, fechaFin, estamento);
    }

    public List<ReporteTendencia> tendenciaSemanal(Long rutaId, LocalDate fechaInicio,
                                                   LocalDate fechaFin) {
        return reporteAsistenciaPort.obtenerTendenciaSemanal(rutaId, fechaInicio, fechaFin);
    }

    public List<ReporteAsistenciaDetalle> detalle(Long rutaId, String programaAcademico,
                                                  Integer semestre, String estamento,
                                                  LocalDate fechaInicio, LocalDate fechaFin) {
        return reporteAsistenciaPort.obtenerDetalleAsistencia(
                rutaId, programaAcademico, semestre, estamento, fechaInicio, fechaFin);
    }
}