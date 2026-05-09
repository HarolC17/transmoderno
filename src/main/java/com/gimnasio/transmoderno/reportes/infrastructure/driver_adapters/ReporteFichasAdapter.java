package com.gimnasio.transmoderno.reportes.infrastructure.driver_adapters;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteDistribucionPost;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteFichas;
import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteFichasPort;
import jakarta.persistence.EntityManager;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ReporteFichasAdapter implements ReporteFichasPort {

    private final EntityManager entityManager;

    @Override
    public List<ReporteFichas> obtenerComparativaPrePost(Long rutaId,
                                                         String programaAcademico) {

        StringBuilder jpql = new StringBuilder("""
            SELECT pregPre.orden,
                   pregPre.texto,
                   AVG(CAST(rpre.valor AS Double))
            FROM PreguntaData pregPre
            JOIN RespuestaFichaPreData rpre ON rpre.preguntaId = pregPre.id
            JOIN FichaPreData          fpre ON fpre.id          = rpre.fichaPreId
            JOIN InscripcionData       i    ON i.id             = fpre.inscripcionId
            JOIN ParticipanteData      p    ON p.id             = i.participanteId
            WHERE fpre.completada  = true
              AND pregPre.tipFicha = 'PRE'
              AND pregPre.tipo     IN ('ESCALA_1_5', 'ESCALA_0_4')
              AND pregPre.activa   = true
            """);

        if (rutaId != null)            jpql.append(" AND i.rutaId = :rutaId");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");

        jpql.append(" GROUP BY pregPre.id, pregPre.texto, pregPre.orden ORDER BY pregPre.orden");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null)            query.setParameter("rutaId", rutaId);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);

        List<Object[]> resultados = query.getResultList();
        List<ReporteFichas> reportes = new ArrayList<>();
        for (Object[] row : resultados) {
            reportes.add(new ReporteFichas(
                    ((Number) row[0]).intValue(),
                    (String)  row[1],
                    (Double)  row[2],
                    null   // promedioPost — se manejará con endpoint separado
            ));
        }
        return reportes;
    }

    @Override
    public List<ReporteDistribucionPost> obtenerDistribucionPost(Long rutaId, String programaAcademico) {

        StringBuilder jpql = new StringBuilder("""
            SELECT p.orden,
                   p.texto,
                   rpost.valor,
                   COUNT(rpost.id)
            FROM PreguntaData p
            JOIN RespuestaFichaPostData rpost ON rpost.preguntaId = p.id
            JOIN FichaPostData          fpost ON fpost.id          = rpost.fichaPostId
            JOIN FichaPreData           fpre  ON fpre.id           = fpost.fichaPreId
            JOIN InscripcionData        i     ON i.id              = fpre.inscripcionId
            JOIN ParticipanteData       part  ON part.id           = i.participanteId
            WHERE fpost.completada = true
              AND p.tipFicha       = 'POST'
              AND p.tipo           = 'SELECCION'
              AND p.activa         = true
              AND p.orden          BETWEEN 6 AND 33
            """);

        if (rutaId != null)            jpql.append(" AND i.rutaId = :rutaId");
        if (programaAcademico != null) jpql.append(" AND part.programaAcademico = :programaAcademico");

        jpql.append(" GROUP BY p.id, p.orden, p.texto, rpost.valor ORDER BY p.orden, rpost.valor");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null)            query.setParameter("rutaId", rutaId);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);

        List<Object[]> rows = query.getResultList();

        // Agrupar por pregunta
        Map<Integer, ReporteDistribucionPost> mapa = new LinkedHashMap<>();
        for (Object[] row : rows) {
            Integer orden  = ((Number) row[0]).intValue();
            String  texto  = (String)  row[1];
            String  valor  = (String)  row[2];
            Long    count  = ((Number) row[3]).longValue();

            mapa.computeIfAbsent(orden, k -> ReporteDistribucionPost.builder()
                    .orden(orden)
                    .pregunta(texto)
                    .distribucion(new LinkedHashMap<>())
                    .total(0L)
                    .build());

            ReporteDistribucionPost rep = mapa.get(orden);
            rep.getDistribucion().put(valor, count);
            rep.setTotal(rep.getTotal() + count);
        }

        return new ArrayList<>(mapa.values());
    }
}