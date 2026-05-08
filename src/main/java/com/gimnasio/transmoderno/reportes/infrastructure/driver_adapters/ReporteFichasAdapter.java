package com.gimnasio.transmoderno.reportes.infrastructure.driver_adapters;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteFichas;
import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteFichasPort;
import jakarta.persistence.EntityManager;
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
                       AVG(CAST(rpre.valor AS Double)),
                       AVG(CAST(rpost.valor AS Double))
                FROM PreguntaData pregPre
                JOIN PreguntaData pregPost
                     ON pregPost.orden    = pregPre.orden
                    AND pregPost.rutaId   = pregPre.rutaId
                    AND pregPost.tipFicha = 'POST'
                JOIN RespuestaFichaPreData  rpre  ON rpre.preguntaId  = pregPre.id
                JOIN FichaPreData           fpre  ON fpre.id           = rpre.fichaPreId
                JOIN FichaPostData          fpost ON fpost.fichaPreId  = fpre.id
                JOIN RespuestaFichaPostData rpost ON rpost.fichaPostId = fpost.id
                                                 AND rpost.preguntaId  = pregPost.id
                JOIN InscripcionData  i ON i.id = fpre.inscripcionId
                JOIN ParticipanteData p ON p.id = i.participanteId
                WHERE fpre.completada  = true
                  AND fpost.completada = true
                  AND pregPre.tipFicha = 'PRE'
                  AND pregPre.tipo    <> 'NUMERO'
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
                    ((Number) row[0]).intValue(),   // orden
                    (String)  row[1],               // pregunta
                    (Double)  row[2],               // promedioPre
                    (Double)  row[3]                // promedioPost
            ));
        }
        return reportes;
    }
}