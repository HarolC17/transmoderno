package com.gimnasio.transmoderno.participantes.domain.usecase;

import com.gimnasio.transmoderno.participantes.domain.model.Participante;
import com.gimnasio.transmoderno.participantes.domain.model.port.ParticipanteRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ObtenerParticipantesUseCase {

    private final ParticipanteRepository participanteRepository;

    public List<Participante> ejecutar(int page, int size) {
        return participanteRepository.findAll(page, size);
    }

    public long contarTotal() {
        return participanteRepository.count();
    }
}