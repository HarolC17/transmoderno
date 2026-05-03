package com.gimnasio.transmoderno.alertas.domain.usecase;

import com.gimnasio.transmoderno.alertas.domain.model.AlertaInasistencia;
import com.gimnasio.transmoderno.alertas.domain.model.SolicitudAyuda;
import com.gimnasio.transmoderno.alertas.domain.model.port.AlertaInasistenciaPort;
import com.gimnasio.transmoderno.alertas.domain.model.port.SolicitudAyudaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObtenerAlertasUseCaseTest {

    @Mock private AlertaInasistenciaPort alertaInasistenciaPort;
    @InjectMocks private ObtenerAlertasInasistenciaUseCase obtenerAlertasUseCase;

    @Mock private SolicitudAyudaRepository solicitudAyudaRepository;
    @InjectMocks private ObtenerSolicitudesAyudaUseCase obtenerSolicitudesUseCase;

    @Test
    void debeRetornarAlertasInasistencia() {
        List<AlertaInasistencia> alertas = List.of(
                AlertaInasistencia.builder().participanteId(1L).nivelRiesgo("ALTO").build()
        );
        when(alertaInasistenciaPort.obtenerParticipantesSinAsistir(1, 0, 10, null, null))
                .thenReturn(alertas);

        List<AlertaInasistencia> resultado = obtenerAlertasUseCase.ejecutar(0, 10, null, null);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void debeContarTotalAlertas() {
        when(alertaInasistenciaPort.contarParticipantesSinAsistir(1, null, null)).thenReturn(5L);

        long total = obtenerAlertasUseCase.contarTotal(null, null);

        assertEquals(5L, total);
    }

    @Test
    void debeRetornarSolicitudesAyuda() {
        List<SolicitudAyuda> solicitudes = List.of(
                SolicitudAyuda.builder().id(1L).participanteId(1L).atendida(false).build()
        );
        when(solicitudAyudaRepository.findAll(0, 10)).thenReturn(solicitudes);

        List<SolicitudAyuda> resultado = obtenerSolicitudesUseCase.ejecutar(0, 10);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void debeContarTotalSolicitudes() {
        when(solicitudAyudaRepository.count()).thenReturn(3L);

        long total = obtenerSolicitudesUseCase.contarTotal();

        assertEquals(3L, total);
    }
}