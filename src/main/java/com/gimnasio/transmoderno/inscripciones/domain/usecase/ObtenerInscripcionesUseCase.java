package com.gimnasio.transmoderno.inscripciones.domain.usecase;

import com.gimnasio.transmoderno.inscripciones.domain.model.Inscripcion;
import com.gimnasio.transmoderno.inscripciones.domain.model.port.InscripcionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ObtenerInscripcionesUseCase {

    private final InscripcionRepository inscripcionRepository;

    public List<Inscripcion> ejecutar(int page, int size) {
        return inscripcionRepository.findAll(page, size);
    }

    public List<Inscripcion> ejecutarPorRuta(Long rutaId, int page, int size) {
        return inscripcionRepository.findAllByRutaId(rutaId, page, size);
    }

    public long contarTotal() {
        return inscripcionRepository.count();
    }

    public long contarPorRuta(Long rutaId) {
        return inscripcionRepository.countByRutaId(rutaId);
    }
}