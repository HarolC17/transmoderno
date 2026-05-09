package com.gimnasio.transmoderno.alertas.domain.usecase;

import com.gimnasio.transmoderno.alertas.domain.model.SolicitudAyuda;
import com.gimnasio.transmoderno.alertas.domain.model.port.SolicitudAyudaRepository;
import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.model.port.ParticipanteRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ObtenerSolicitudesAyudaUseCase {

    private final SolicitudAyudaRepository solicitudAyudaRepository;
    private final ParticipanteRepository   participanteRepository;

    public List<SolicitudAyuda> ejecutar(int page, int size) {
        List<SolicitudAyuda> solicitudes = solicitudAyudaRepository.findAll(page, size);
        solicitudes.forEach(s -> {
            try {
                participanteRepository.findById(s.getParticipanteId()).ifPresent(p -> {
                    s.setNombreCompleto(p.getNombreCompleto());
                    s.setNumeroIdentificacion(p.getNumeroIdentificacion());
                    s.setProgramaAcademico(p.getProgramaAcademico());
                    s.setCorreoInstitucional(p.getCorreoInstitucional());
                    s.setTelefono(p.getTelefono());
                });
            } catch (Exception ignored) {}
        });
        return solicitudes;
    }

    public long contarTotal() {
        return solicitudAyudaRepository.count();
    }
}