package com.gimnasio.transmoderno.reportes.application;

import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteRepository;
import com.gimnasio.transmoderno.reportes.domain.usecase.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReportesUseCaseConfig {

    @Bean
    public ObtenerReporteAsistenciaSesionesUseCase obtenerReporteAsistenciaSesionesUseCase(
            ReporteRepository reporteRepository) {
        return new ObtenerReporteAsistenciaSesionesUseCase(reporteRepository);
    }

    @Bean
    public ObtenerReporteAsistenciaRutaUseCase obtenerReporteAsistenciaRutaUseCase(
            ReporteRepository reporteRepository) {
        return new ObtenerReporteAsistenciaRutaUseCase(reporteRepository);
    }

    @Bean
    public ObtenerReporteComparativoUseCase obtenerReporteComparativoUseCase(
            ReporteRepository reporteRepository) {
        return new ObtenerReporteComparativoUseCase(reporteRepository);
    }

    @Bean
    public ObtenerReporteAsistenciaPeriodoUseCase obtenerReporteAsistenciaPeriodoUseCase(
            ReporteRepository reporteRepository) {
        return new ObtenerReporteAsistenciaPeriodoUseCase(reporteRepository);
    }

    @Bean
    public ObtenerReporteComparativoEntreRutasUseCase obtenerReporteComparativoEntreRutasUseCase(
            ReporteRepository reporteRepository) {
        return new ObtenerReporteComparativoEntreRutasUseCase(reporteRepository);
    }

    @Bean
    public ObtenerReporteGeneralUseCase obtenerReporteGeneralUseCase(
            ReporteRepository reporteRepository) {
        return new ObtenerReporteGeneralUseCase(reporteRepository);
    }
}
