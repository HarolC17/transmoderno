package com.gimnasio.transmoderno.inscripciones.infrastructure.driver_adapters.jpa_repository;

import com.gimnasio.transmoderno.inscripciones.domain.model.EstadoInscripcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InscripcionJpaRepository extends JpaRepository<InscripcionData, Long> {

    Page<InscripcionData> findByRutaId(Long rutaId, Pageable pageable);
    List<InscripcionData> findByParticipanteId(Long participanteId);
    Optional<InscripcionData> findByParticipanteIdAndRutaId(Long participanteId, Long rutaId);
    long countByRutaId(Long rutaId);
    long countByRutaIdAndEstado(Long rutaId, EstadoInscripcion estado);

    @Query("SELECT i FROM InscripcionData i ORDER BY i.rutaId ASC, CASE i.estado WHEN 'ACTIVA' THEN 0 WHEN 'INACTIVA' THEN 1 WHEN 'FINALIZADA' THEN 2 END ASC")
    Page<InscripcionData> findAllOrdenado(Pageable pageable);

    // ── NUEVOS métodos con filtros combinados ──────────────────────────────────

    @Query("SELECT i FROM InscripcionData i WHERE " +
            "(:rutaId IS NULL OR i.rutaId = :rutaId) AND " +
            "(:estado IS NULL OR i.estado = :estado) " +
            "ORDER BY i.rutaId ASC, CASE i.estado WHEN 'ACTIVA' THEN 0 WHEN 'INACTIVA' THEN 1 WHEN 'FINALIZADA' THEN 2 END ASC")
    Page<InscripcionData> findByFiltros(
            @Param("rutaId") Long rutaId,
            @Param("estado") EstadoInscripcion estado,
            Pageable pageable);

    @Query("SELECT COUNT(i) FROM InscripcionData i WHERE " +
            "(:rutaId IS NULL OR i.rutaId = :rutaId) AND " +
            "(:estado IS NULL OR i.estado = :estado)")
    long countByFiltros(
            @Param("rutaId") Long rutaId,
            @Param("estado") EstadoInscripcion estado);
}