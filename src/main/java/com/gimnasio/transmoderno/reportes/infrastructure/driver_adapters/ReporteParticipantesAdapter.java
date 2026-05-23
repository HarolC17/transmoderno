package com.gimnasio.transmoderno.reportes.infrastructure.driver_adapters;

import com.gimnasio.transmoderno.reportes.domain.model.ReporteCobertura;
import com.gimnasio.transmoderno.reportes.domain.model.ReporteParticipantes;
import com.gimnasio.transmoderno.reportes.domain.model.port.ReporteParticipantesPort;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        if (rutaId != null)            jpql.append(" AND i.rutaId = :rutaId");
        if (semestre != null)          jpql.append(" AND p.semestre = :semestre");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");
        if (estamento != null)         jpql.append(" AND p.estamento = :estamento");
        jpql.append(" GROUP BY p.programaAcademico ORDER BY COUNT(p.id) DESC");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null)            query.setParameter("rutaId", rutaId);
        if (semestre != null)          query.setParameter("semestre", semestre);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);
        if (estamento != null)         query.setParameter("estamento", estamento);

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

        if (rutaId != null)            jpql.append(" AND i.rutaId = :rutaId");
        if (programaAcademico != null) jpql.append(" AND p.programaAcademico = :programaAcademico");
        if (estamento != null)         jpql.append(" AND p.estamento = :estamento");
        jpql.append(" GROUP BY p.semestre ORDER BY p.semestre");

        var query = entityManager.createQuery(jpql.toString());
        if (rutaId != null)            query.setParameter("rutaId", rutaId);
        if (programaAcademico != null) query.setParameter("programaAcademico", programaAcademico);
        if (estamento != null)         query.setParameter("estamento", estamento);

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

        long unica = 0, dos = 0, tresACuatro = 0, cincoMas = 0;
        for (Object[] row : resultados) {
            long sesiones = (Long) row[1];
            if (sesiones == 1)      unica++;
            else if (sesiones == 2) dos++;
            else if (sesiones <= 4) tresACuatro++;
            else                    cincoMas++;
        }

        List<ReporteParticipantes> reportes = new ArrayList<>();
        if (unica > 0)       reportes.add(new ReporteParticipantes("1 sesión", unica));
        if (dos > 0)         reportes.add(new ReporteParticipantes("2 sesiones", dos));
        if (tresACuatro > 0) reportes.add(new ReporteParticipantes("3-4 sesiones", tresACuatro));
        if (cincoMas > 0)    reportes.add(new ReporteParticipantes("5 o más", cincoMas));
        return reportes;
    }

    @Override
    public List<ReporteCobertura> obtenerCoberturaPorPrograma() {
        // Query 1 — total GLOBAL de la institución (los 13.346)
        Long totalInstitucion = (Long) entityManager
                .createQuery("SELECT COUNT(e.documento) FROM EstudianteUcundinamarcaData e")
                .getSingleResult();

        // Query 2 — matriculados por pensum con clave normalizada
        String jpqlMatriculados = """
                SELECT e.pensum, COUNT(e.documento)
                FROM EstudianteUcundinamarcaData e
                WHERE e.pensum IS NOT NULL
                GROUP BY e.pensum
                """;

        Map<String, Long> matriculadosPorPrograma = new HashMap<>();
        List<Object[]> resMatriculados = entityManager.createQuery(jpqlMatriculados).getResultList();
        for (Object[] row : resMatriculados) {
            String clave = normalizar((String) row[0]);
            matriculadosPorPrograma.merge(clave, (Long) row[1], Long::sum);
        }

        // Query 3 — participantes activos por programa con clave normalizada
        String jpqlParticipantes = """
                SELECT p.programaAcademico, COUNT(DISTINCT p.id)
                FROM ParticipanteData p
                JOIN InscripcionData i ON i.participanteId = p.id
                WHERE i.estado = 'ACTIVA'
                AND p.programaAcademico IS NOT NULL
                GROUP BY p.programaAcademico
                """;

        // clave normalizada → [nombreOriginal, count]
        Map<String, Object[]> participantesPorPrograma = new HashMap<>();
        List<Object[]> resParticipantes = entityManager.createQuery(jpqlParticipantes).getResultList();
        for (Object[] row : resParticipantes) {
            String nombreOriginal = (String) row[0];
            String clave          = normalizar(nombreOriginal);
            participantesPorPrograma.put(clave, new Object[]{ nombreOriginal, (Long) row[1] });
        }

        // Cruce con clave normalizada — guiones y espacios ya no causan mismatches
        List<ReporteCobertura> cobertura = new ArrayList<>();
        for (Map.Entry<String, Object[]> entry : participantesPorPrograma.entrySet()) {
            String clave          = entry.getKey();
            String nombreOriginal = (String) entry.getValue()[0];
            long participantes    = (Long)   entry.getValue()[1];
            long matriculados     = matriculadosPorPrograma.getOrDefault(clave, 0L);
            double porcentaje     = matriculados > 0
                    ? Math.round((participantes * 100.0 / matriculados) * 100.0) / 100.0
                    : 0.0;
            cobertura.add(new ReporteCobertura(
                    nombreOriginal, matriculados, participantes, porcentaje, totalInstitucion));
        }

        cobertura.sort((a, b) -> Double.compare(b.getPorcentaje(), a.getPorcentaje()));
        return cobertura;
    }

    /**
     * Normaliza nombres de programas para el cruce entre tablas.
     * Ejemplos que ahora coinciden:
     *   "INGENIERIA DE SISTEMAS Y COMPUTACION 2020 - FUSAGASUGA"
     *   "INGENIERIA DE SISTEMAS Y COMPUTACION 2020  FUSAGASUGA"
     *   → "ingenieria de sistemas y computacion 2020 fusagasuga"
     */
// DESPUÉS — agrega este import al inicio del archivo:
// import java.text.Normalizer;

    private String normalizar(String texto) {
        if (texto == null) return "";
        // 1. Descomponer acentos (á → a + ́) y eliminarlos
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        // 2. Quitar guion, colapsar espacios, minúsculas
        return sinAcentos
                .toLowerCase()
                .trim()
                .replaceAll("\\s*-\\s*", " ")
                .replaceAll("\\s+", " ");
    }
}