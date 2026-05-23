package com.gimnasio.transmoderno.reportes.infrastructure.driver_adapters;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteParticipantes;
import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteParticipantesPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReporteParticipantesAdapter implements ReporteParticipantesPort {

    private final EntityManager entityManager;

    @Override
    public List<ReporteParticipantes> obtenerDistribucionPorPrograma(Long rutaId, Integer semestre,
                                                                     String programaAcademico, String estamento) {
        StringBuilder jpql = new StringBuilder("""
                SELECT p.programaAcademico, COUNT(p.id)
                FROM ParticipanteData p
                JOIN InscripcionData i ON i.participanteId = p.id
                WHERE i.estado = 'ACTIVA'
                """);

        if (rutaId != null) jpql.append(" AND i.rutaId = :rutaId");
        if (semestre != null) jpql.append(" AND p.semestre = :semestre");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");
        if (estamento != null) jpql.append(" AND p.estamento = :estamento");
        jpql.append(" GROUP BY p.programaAcademico ORDER BY COUNT(p.id) DESC");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        if (semestre != null) query.setParameter("semestre", semestre);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);
        if (estamento != null) query.setParameter("estamento", estamento);

        List<Object[]> resultados = query.getResultList();
        List<ReporteParticipantes> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteParticipantes((String) row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteParticipantes> obtenerDistribucionPorSemestre(Long rutaId,
                                                                     String programaAcademico, String estamento) {
        StringBuilder jpql = new StringBuilder("""
                SELECT CAST(p.semestre AS string), COUNT(p.id)
                FROM ParticipanteData p
                JOIN InscripcionData i ON i.participanteId = p.id
                WHERE i.estado = 'ACTIVA'
                """);

        if (rutaId != null) jpql.append(" AND i.rutaId = :rutaId");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");
        if (estamento != null) jpql.append(" AND p.estamento = :estamento");
        jpql.append(" GROUP BY p.semestre ORDER BY p.semestre");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);
        if (estamento != null) query.setParameter("estamento", estamento);

        List<Object[]> resultados = query.getResultList();
        List<ReporteParticipantes> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteParticipantes("Semestre " + row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteParticipantes> obtenerParticipantesPorRuta() {
        String jpql = """
                SELECT r.nombre, COUNT(i.id)
                FROM InscripcionData i
                JOIN RutaData r ON r.id = i.rutaId
                WHERE i.estado = 'ACTIVA'
                GROUP BY r.nombre
                ORDER BY COUNT(i.id) DESC
                """;

        var query = entityManager.createQuery(jpql);
        List<Object[]> resultados = query.getResultList();
        List<ReporteParticipantes> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteParticipantes((String) row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteParticipantes> obtenerDistribucionPorMotivo(Long rutaId) {
        StringBuilder jpql = new StringBuilder("""
            SELECT i.motivo, COUNT(i.id)
            FROM InscripcionData i
            WHERE i.motivo IS NOT NULL
            AND (i.estado = 'INACTIVA' OR i.estado = 'FINALIZADA')
            """);

        if (rutaId != null) jpql.append(" AND i.rutaId = :rutaId");
        jpql.append(" GROUP BY i.motivo ORDER BY COUNT(i.id) DESC");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);

        List<Object[]> resultados = query.getResultList();
        List<ReporteParticipantes> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteParticipantes((String) row[0], (Long) row[1]));
        }
        return reportes;
    }

    @Override
    public List<ReporteParticipantes> obtenerNivelRecurrencia(Long rutaId) {
        // Paso 1: contar sesiones asistidas por cada participante
        StringBuilder jpql = new StringBuilder("""
                SELECT ra.participanteId, COUNT(ra.id)
                FROM RegistroAsistenciaData ra
                JOIN InscripcionData i ON i.participanteId = ra.participanteId
                WHERE i.estado = 'ACTIVA'
                """);

        if (rutaId != null) jpql.append(" AND i.rutaId = :rutaId");
        jpql.append(" GROUP BY ra.participanteId");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null) query.setParameter("rutaId", rutaId);

        List<Object[]> resultados = query.getResultList();

        // Paso 2: clasificar en buckets en Java
        long unica = 0, dos = 0, tresACuatro = 0, cincoMas = 0;
        for (Object[] row : resultados) {
            long sesiones = (Long) row[1];
            if (sesiones == 1)       unica++;
            else if (sesiones == 2)  dos++;
            else if (sesiones <= 4)  tresACuatro++;
            else                     cincoMas++;
        }

        // Paso 3: devolver solo categorías con participantes
        List<ReporteParticipantes> reportes = new ArrayList<>();
        if (unica > 0)       reportes.add(new ReporteParticipantes("1 sesión", unica));
        if (dos > 0)         reportes.add(new ReporteParticipantes("2 sesiones", dos));
        if (tresACuatro > 0) reportes.add(new ReporteParticipantes("3-4 sesiones", tresACuatro));
        if (cincoMas > 0)    reportes.add(new ReporteParticipantes("5 o más", cincoMas));
        return reportes;
    }
}