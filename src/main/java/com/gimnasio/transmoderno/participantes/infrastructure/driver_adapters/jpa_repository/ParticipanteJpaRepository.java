package com.gimnasio.transmoderno.participantes.infrastructure.driver_adapters.jpa_repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface ParticipanteJpaRepository extends JpaRepository<ParticipanteData, Long> {
    Optional<ParticipanteData> findByNumeroIdentificacion(String numeroIdentificacion);
    Page<ParticipanteData> findAll(Pageable pageable);

    // ❌ Quitar estos dos
    // Page<ParticipanteData> findByNombreCompletoContainingIgnoreCase(String nombre, Pageable pageable);
    // long countByNombreCompletoContainingIgnoreCase(String nombre);

    // ✅ Reemplazar por estos — busca en nombre E identificación
    @Query("SELECT p FROM ParticipanteData p WHERE " +
            "LOWER(p.nombreCompleto) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "p.numeroIdentificacion LIKE CONCAT('%', :termino, '%')")
    Page<ParticipanteData> findByNombreOrIdentificacion(@Param("termino") String termino, Pageable pageable);

    @Query("SELECT COUNT(p) FROM ParticipanteData p WHERE " +
            "LOWER(p.nombreCompleto) LIKE LOWER(CONCAT('%', :termino, '%')) OR " +
            "p.numeroIdentificacion LIKE CONCAT('%', :termino, '%')")
    long countByNombreOrIdentificacion(@Param("termino") String termino);
}