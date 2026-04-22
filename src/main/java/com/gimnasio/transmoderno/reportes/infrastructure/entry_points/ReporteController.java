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
    private final ObtenerReporteAsistenciaPeriodoUseCase asistenciaPeriodoUseCase;
    private final ObtenerReporteComparativoEntreRutasUseCase comparativoEntreRutasUseCase;
    private final ObtenerReporteGeneralUseCase reporteGeneralUseCase;

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

    /**
     * GET /api/reportes/asistencia/periodo/{rutaId}?desde=yyyy-MM-dd&hasta=yyyy-MM-dd
     * Asistencia de una ruta en un período específico con detalle por sesión.
     * Acceso: ADMIN, ENCARGADO
     */
    @GetMapping("/asistencia/periodo/{rutaId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ENCARGADO')")
    public ResponseEntity<ReporteAsistenciaPeriodoResponse> asistenciaPorPeriodo(
            @PathVariable Long rutaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        var reporte = asistenciaPeriodoUseCase.ejecutar(rutaId, desde, hasta);

        List<ReporteAsistenciaSesionResponse> detalle = reporte.getDetallePorSesion().stream()
                .map(s -> new ReporteAsistenciaSesionResponse(
                        s.getSesionId(), s.getNombreSesion(), s.getFecha(),
                        s.getRutaId(), s.getNombreRuta(),
                        s.getTotalAsistentes(), s.getTotalInscritos(), s.getPorcentajeAsistencia()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ReporteAsistenciaPeriodoResponse(
                reporte.getRutaId(),
                reporte.getNombreRuta(),
                reporte.getDesde(),
                reporte.getHasta(),
                reporte.getTotalSesionesEnPeriodo(),
                reporte.getTotalInscritos(),
                reporte.getTotalRegistrosAsistencia(),
                reporte.getPromedioAsistenciaPorSesion(),
                reporte.getPorcentajeAsistenciaGeneral(),
                detalle
        ));
    }

    /**
     * GET /api/reportes/comparativo/rutas
     * Comparativo de asistencia y bienestar entre todas las rutas activas.
     * Acceso: ADMIN
     */
    @GetMapping("/comparativo/rutas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteComparativoEntreRutasResponse> comparativoEntreRutas() {

        var reporte = comparativoEntreRutasUseCase.ejecutar();

        List<ReporteAsistenciaRutaResponse> asistencia = reporte.getAsistenciaPorRuta().stream()
                .map(r -> new ReporteAsistenciaRutaResponse(
                        r.getRutaId(), r.getNombreRuta(), r.getTotalSesiones(),
                        r.getTotalInscritos(), r.getTotalRegistrosAsistencia(),
                        r.getPromedioAsistenciaPorSesion(), r.getPorcentajeAsistenciaGeneral()
                ))
                .collect(Collectors.toList());

        List<ReporteComparativoRutaResponse> bienestar = reporte.getBienestarPorRuta().stream()
                .map(r -> {
                    List<ReporteComparativoPreguntaResponse> preguntas = r.getPorPregunta().stream()
                            .map(p -> new ReporteComparativoPreguntaResponse(
                                    p.getPreguntaId(), p.getTextoPregunta(), p.getOrdenPregunta(),
                                    p.getPromedioPre(), p.getPromedioPost(), p.getDiferencia()
                            ))
                            .collect(Collectors.toList());
                    return new ReporteComparativoRutaResponse(
                            r.getRutaId(), r.getNombreRuta(), r.getTotalParticipantesConAmbas(),
                            r.getPromedioGeneralPre(), r.getPromedioGeneralPost(),
                            r.getDiferenciaGeneral(), preguntas
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ReporteComparativoEntreRutasResponse(
                reporte.getTotalRutas(),
                asistencia,
                bienestar,
                reporte.getRutaMayorAsistencia(),
                reporte.getRutaMayorMejoraBienestar()
        ));
    }

    /**
     * GET /api/reportes/general
     * Resumen global del sistema: participantes, rutas, inscripciones,
     * asistencia, fichas y alertas.
     * Acceso: ADMIN
     */
    @GetMapping("/general")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteGeneralResponse> reporteGeneral() {

        var reporte = reporteGeneralUseCase.ejecutar();

        return ResponseEntity.ok(new ReporteGeneralResponse(
                reporte.getTotalParticipantes(),
                reporte.getTotalParticipantesActivos(),
                reporte.getTotalRutas(),
                reporte.getTotalRutasActivas(),
                reporte.getTotalInscripciones(),
                reporte.getTotalInscripcionesActivas(),
                reporte.getTotalSesiones(),
                reporte.getTotalRegistrosAsistencia(),
                reporte.getPromedioAsistenciaGlobal(),
                reporte.getTotalFichasPreCompletadas(),
                reporte.getTotalFichasPostCompletadas(),
                reporte.getTotalParticipantesConCicloCompleto(),
                reporte.getTotalSolicitudesAyuda(),
                reporte.getTotalSolicitudesAyudaAtendidas(),
                reporte.getTotalSolicitudesAyudaPendientes()
        ));
    }
}
