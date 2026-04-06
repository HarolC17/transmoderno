package com.gimnasio.transmoderno.reportes.infrastructure.entry_points;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistenciaRuta;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistenciaSesion;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteComparativoRuta;
import com.gimnasio.transmoderno.reportes.domain.usecase.*;
import com.gimnasio.transmoderno.reportes.infrastructure.entry_points.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ObtenerReporteAsistenciaSesionesUseCase asistenciaSesionesUseCase;
    private final ObtenerReporteAsistenciaRutaUseCase asistenciaRutaUseCase;
    private final ObtenerReporteComparativoUseCase comparativoUseCase;

    /**
     * GET /api/reportes/asistencia/sesiones/{rutaId}
     * Asistencia por sesión dentro de una ruta.
     * Parámetros opcionales: desde, hasta (yyyy-MM-dd)
     * Acceso: ADMIN, ENCARGADO
     */
    @GetMapping("/asistencia/sesiones/{rutaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<List<ReporteAsistenciaSesionResponse>> asistenciaPorSesiones(
            @PathVariable Long rutaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<ReporteAsistenciaSesion> reporte = asistenciaSesionesUseCase.ejecutar(rutaId, desde, hasta);

        List<ReporteAsistenciaSesionResponse> respuesta = reporte.stream()
                .map(r -> new ReporteAsistenciaSesionResponse(
                        r.getSesionId(),
                        r.getNombreSesion(),
                        r.getFecha(),
                        r.getRutaId(),
                        r.getNombreRuta(),
                        r.getTotalAsistentes(),
                        r.getTotalInscritos(),
                        r.getPorcentajeAsistencia()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * GET /api/reportes/asistencia/ruta/{rutaId}
     * Resumen general de asistencia de una ruta.
     * Acceso: ADMIN, ENCARGADO
     */
    @GetMapping("/asistencia/ruta/{rutaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ReporteAsistenciaRutaResponse> asistenciaPorRuta(
            @PathVariable Long rutaId) {

        ReporteAsistenciaRuta reporte = asistenciaRutaUseCase.ejecutar(rutaId);

        return ResponseEntity.ok(new ReporteAsistenciaRutaResponse(
                reporte.getRutaId(),
                reporte.getNombreRuta(),
                reporte.getTotalSesiones(),
                reporte.getTotalInscritos(),
                reporte.getTotalRegistrosAsistencia(),
                reporte.getPromedioAsistenciaPorSesion(),
                reporte.getPorcentajeAsistenciaGeneral()
        ));
    }

    /**
     * GET /api/reportes/comparativo/{rutaId}
     * Comparativa PRE vs POST de indicadores de bienestar por ruta.
     * Acceso: ADMIN, PSICOLOGO
     */
    @GetMapping("/comparativo/{rutaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PSICOLOGO')")
    public ResponseEntity<ReporteComparativoRutaResponse> comparativoPrePost(
            @PathVariable Long rutaId) {

        ReporteComparativoRuta reporte = comparativoUseCase.ejecutar(rutaId);

        List<ReporteComparativoPreguntaResponse> porPregunta = reporte.getPorPregunta()
                .stream()
                .map(p -> new ReporteComparativoPreguntaResponse(
                        p.getPreguntaId(),
                        p.getTextoPregunta(),
                        p.getOrdenPregunta(),
                        p.getPromedioPre(),
                        p.getPromedioPost(),
                        p.getDiferencia()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ReporteComparativoRutaResponse(
                reporte.getRutaId(),
                reporte.getNombreRuta(),
                reporte.getTotalParticipantesConAmbas(),
                reporte.getPromedioGeneralPre(),
                reporte.getPromedioGeneralPost(),
                reporte.getDiferenciaGeneral(),
                porPregunta
        ));
    }
}
