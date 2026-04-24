package com.gimnasio.transmoderno.reportes.infrastructure.driver_adapters;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistencia;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteAsistenciaDetalle;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteTendencia;
import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteAsistenciaPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReporteAsistenciaAdapter implements ReporteAsistenciaPort {

    private final EntityManager entityManager;

    @Override
    public List<ReporteAsistencia> obtenerAsistenciaPorRuta(Long rutaId,
                                                            String programaAcademico, Integer semestre,
                                                            LocalDate fechaInicio, LocalDate fechaFin,
                                                            String estamento) {
        StringBuilder jpql = new StringBuilder("""
                SELECT r.nombre, COUNT(a.id)
                FROM RegistroAsistenciaData a
                JOIN SesionData s ON s.id = a.sesionId
                JOIN RutaData r ON r.id = s.rutaId
                JOIN ParticipanteData p ON p.id = a.participanteId
                WHERE 1=1
                """);

        if (rutaId != null) jpql.append(" AND r.id = :rutaId");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");
        if (semestre != null) jpql.append(" AND p.semestre = :semestre");
        if (fechaInicio != null) jpql.append(" AND s.fecha >= :fechaInicio");
        if (fechaFin != null) jpql.append(" AND s.fecha <= :fechaFin");
        if (estamento != null) jpql.append(" AND p.estamento = :estamento");
        jpql.append(" GROUP BY r.nombre ORDER BY r.nombre");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);
        if (semestre != null) query.setParameter("semestre", semestre);
        if (fechaInicio != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin != null) query.setParameter("fechaFin", fechaFin);
        if (estamento != null) query.setParameter("estamento", estamento);

        List<Object[]> resultados = query.getResultList();
        List<ReporteAsistencia> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteAsistencia((String) row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteAsistencia> obtenerAsistenciaPorPrograma(Long rutaId,
                                                                Integer semestre, LocalDate fechaInicio,
                                                                LocalDate fechaFin, String estamento) {
        StringBuilder jpql = new StringBuilder("""
                SELECT p.programaAcademico, COUNT(a.id)
                FROM RegistroAsistenciaData a
                JOIN SesionData s ON s.id = a.sesionId
                JOIN ParticipanteData p ON p.id = a.participanteId
                WHERE 1=1
                """);

        if (rutaId != null) jpql.append(" AND s.rutaId = :rutaId");
        if (semestre != null) jpql.append(" AND p.semestre = :semestre");
        if (fechaInicio != null) jpql.append(" AND s.fecha >= :fechaInicio");
        if (fechaFin != null) jpql.append(" AND s.fecha <= :fechaFin");
        if (estamento != null) jpql.append(" AND p.estamento = :estamento");
        jpql.append(" GROUP BY p.programaAcademico ORDER BY COUNT(a.id) DESC");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        if (semestre != null) query.setParameter("semestre", semestre);
        if (fechaInicio != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin != null) query.setParameter("fechaFin", fechaFin);
        if (estamento != null) query.setParameter("estamento", estamento);

        List<Object[]> resultados = query.getResultList();
        List<ReporteAsistencia> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteAsistencia((String) row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteAsistencia> obtenerAsistenciaPorSemestre(Long rutaId,
                                                                String programaAcademico, LocalDate fechaInicio,
                                                                LocalDate fechaFin, String estamento) {
        StringBuilder jpql = new StringBuilder("""
                SELECT CAST(p.semestre AS string), COUNT(a.id)
                FROM RegistroAsistenciaData a
                JOIN SesionData s ON s.id = a.sesionId
                JOIN ParticipanteData p ON p.id = a.participanteId
                WHERE 1=1
                """);

        if (rutaId != null) jpql.append(" AND s.rutaId = :rutaId");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");
        if (fechaInicio != null) jpql.append(" AND s.fecha >= :fechaInicio");
        if (fechaFin != null) jpql.append(" AND s.fecha <= :fechaFin");
        if (estamento != null) jpql.append(" AND p.estamento = :estamento");
        jpql.append(" GROUP BY p.semestre ORDER BY p.semestre");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);
        if (fechaInicio != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin != null) query.setParameter("fechaFin", fechaFin);
        if (estamento != null) query.setParameter("estamento", estamento);

        List<Object[]> resultados = query.getResultList();
        List<ReporteAsistencia> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteAsistencia("Semestre " + row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteTendencia> obtenerTendenciaSemanal(Long rutaId,
                                                          LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder jpql = new StringBuilder("""
                SELECT s.fecha, COUNT(a.id)
                FROM RegistroAsistenciaData a
                JOIN SesionData s ON s.id = a.sesionId
                WHERE 1=1
                """);

        if (rutaId != null) jpql.append(" AND s.rutaId = :rutaId");
        if (fechaInicio != null) jpql.append(" AND s.fecha >= :fechaInicio");
        if (fechaFin != null) jpql.append(" AND s.fecha <= :fechaFin");
        jpql.append(" GROUP BY s.fecha ORDER BY s.fecha");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        if (fechaInicio != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin != null) query.setParameter("fechaFin", fechaFin);

        List<Object[]> resultados = query.getResultList();
        List<ReporteTendencia> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteTendencia((LocalDate) row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteAsistenciaDetalle> obtenerDetalleAsistencia(Long rutaId, String programaAcademico,
                                                                   Integer semestre, String estamento,
                                                                   LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder jpql = new StringBuilder("""
            SELECT p.nombreCompleto, p.numeroIdentificacion, p.programaAcademico,
                   p.semestre, p.estamento, r.nombre, s.nombre, s.fecha
            FROM RegistroAsistenciaData a
            JOIN SesionData s ON s.id = a.sesionId
            JOIN RutaData r ON r.id = s.rutaId
            JOIN ParticipanteData p ON p.id = a.participanteId
            WHERE 1=1
            """);

        if (rutaId != null) jpql.append(" AND r.id = :rutaId");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");
        if (semestre != null) jpql.append(" AND p.semestre = :semestre");
        if (estamento != null) jpql.append(" AND p.estamento = :estamento");
        if (fechaInicio != null) jpql.append(" AND s.fecha >= :fechaInicio");
        if (fechaFin != null) jpql.append(" AND s.fecha <= :fechaFin");
        jpql.append(" ORDER BY s.fecha DESC, r.nombre, p.nombreCompleto");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);
        if (semestre != null) query.setParameter("semestre", semestre);
        if (estamento != null) query.setParameter("estamento", estamento);
        if (fechaInicio != null) query.setParameter("fechaInicio", fechaInicio);
        if (fechaFin != null) query.setParameter("fechaFin", fechaFin);

        List<Object[]> resultados = query.getResultList();
        List<ReporteAsistenciaDetalle> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteAsistenciaDetalle(
                    (String) row[0],
                    (String) row[1],
                    (String) row[2],
                    row[3] != null ? (Integer) row[3] : null,
                    (String) row[4],
                    (String) row[5],
                    (String) row[6],
                    (LocalDate) row[7]
            ));
        }
        return reportes;
    }
}