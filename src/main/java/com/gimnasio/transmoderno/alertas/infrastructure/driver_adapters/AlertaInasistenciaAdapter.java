package com.gimnasio.transmoderno.alertas.infrastructure.driver_adapters;

import com.gimnasio.transmoderno.alertas.domain.model.AlertaInasistencia;
import com.gimnasio.transmoderno.alertas.domain.model.port.AlertaInasistenciaPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlertaInasistenciaAdapter implements AlertaInasistenciaPort {

    private final EntityManager entityManager;

    private static final String SUBQUERY_SESIONES =
            "(SELECT COUNT(*) FROM sesiones s2 WHERE s2.ruta_id = r.id " +
                    "AND s2.fecha > COALESCE(MAX(a.fecha_hora_registro)::date, '1900-01-01'::date))";

    private String construirSQLBase(String nivel, Long rutaId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.numero_identificacion, p.nombre_completo, ");
        sql.append("p.correo_institucional, p.telefono, ");
        sql.append("r.id, r.nombre, MAX(a.fecha_hora_registro), ");
        sql.append(SUBQUERY_SESIONES).append(" AS ses\n");
        sql.append("FROM participantes p\n");
        sql.append("JOIN inscripciones i ON i.participante_id = p.id\n");
        sql.append("JOIN rutas r ON r.id = i.ruta_id\n");
        sql.append("LEFT JOIN registro_asistencia a ON a.participante_id = p.id\n");
        sql.append("    AND a.sesion_id IN (\n");
        sql.append("        SELECT s.id FROM sesiones s WHERE s.ruta_id = r.id\n");
        sql.append("    )\n");
        sql.append("WHERE i.estado = 'ACTIVA'\n");

        if (rutaId != null) sql.append("AND r.id = :rutaId\n");

        sql.append("GROUP BY p.id, p.numero_identificacion, p.nombre_completo, ");
        sql.append("p.correo_institucional, p.telefono, r.id, r.nombre\n");
        sql.append("HAVING ").append(SUBQUERY_SESIONES).append(" >= :sesionesMinimas\n");

        if (nivel != null && !nivel.isEmpty() && !nivel.equals("TODOS")) {
            switch (nivel) {
                case "ALTO" ->
                        sql.append("AND ").append(SUBQUERY_SESIONES).append(" > 4\n");
                case "MODERADO" ->
                        sql.append("AND ").append(SUBQUERY_SESIONES).append(" >= 3 AND ")
                                .append(SUBQUERY_SESIONES).append(" <= 4\n");
                case "LEVE" ->
                        sql.append("AND ").append(SUBQUERY_SESIONES).append(" >= 1 AND ")
                                .append(SUBQUERY_SESIONES).append(" <= 2\n");
            }
        }

        return sql.toString();
    }

    @Override
    public List<AlertaInasistencia> obtenerParticipantesSinAsistir(
            int sesionesMinimas, int page, int size, String nivel, Long rutaId) {

        String orderBy = "ORDER BY CASE " +
                "WHEN " + SUBQUERY_SESIONES + " > 4 THEN 1 " +
                "WHEN " + SUBQUERY_SESIONES + " >= 3 THEN 2 " +
                "ELSE 3 END, p.nombre_completo";

        String sql = construirSQLBase(nivel, rutaId) + orderBy;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sesionesMinimas", sesionesMinimas);
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        query.setFirstResult(page * size);
        query.setMaxResults(size);

        return ((List<Object[]>) query.getResultList()).stream()
                .map(this::mapRow)
                .collect(Collectors.toList());
    }

    @Override
    public long contarParticipantesSinAsistir(int sesionesMinimas, String nivel, Long rutaId) {
        String sqlInner = construirSQLBase(nivel, rutaId);
        String sql = "SELECT COUNT(*) FROM (" + sqlInner + ") conteo";

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("sesionesMinimas", sesionesMinimas);
        if (rutaId != null) query.setParameter("rutaId", rutaId);
        return ((Number) query.getSingleResult()).longValue();
    }

    private AlertaInasistencia mapRow(Object[] row) {
        LocalDateTime ultimaAsistencia = row[7] != null
                ? ((java.sql.Timestamp) row[7]).toLocalDateTime()
                : null;
        long sesionesSinAsistir = row[8] != null ? ((Number) row[8]).longValue() : 0;

        String nivelRiesgo;
        if (sesionesSinAsistir > 4) {
            nivelRiesgo = "ALTO";
        } else if (sesionesSinAsistir >= 3) {
            nivelRiesgo = "MODERADO";
        } else {
            nivelRiesgo = "LEVE";
        }

        return AlertaInasistencia.builder()
                .participanteId(((Number) row[0]).longValue())
                .numeroIdentificacion((String) row[1])
                .nombreCompleto((String) row[2])
                .correoInstitucional((String) row[3])
                .telefono((String) row[4])
                .rutaId(((Number) row[5]).longValue())
                .nombreRuta((String) row[6])
                .ultimaAsistencia(ultimaAsistencia)
                .sesionesSinAsistir(sesionesSinAsistir)
                .nivelRiesgo(nivelRiesgo)
                .build();
    }
}