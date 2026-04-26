package com.gimnasio.transmoderno.sesiones.domain.usecase;

import com.gimnasio.transmoderno.sesiones.domain.exception.FechaSesionInvalidaException;
import com.gimnasio.transmoderno.sesiones.domain.model.Sesion;
import com.gimnasio.transmoderno.sesiones.domain.model.port.SesionRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@RequiredArgsConstructor
public class CrearSesionUseCase {

    private final SesionRepository sesionRepository;

    public Sesion ejecutar(Sesion sesion) {
        LocalDate hoy = LocalDate.now();
        if (sesion.getFecha().isBefore(hoy)) {
            throw new FechaSesionInvalidaException(
                    "No se puede crear una sesión con fecha pasada. La fecha mínima es hoy (" + hoy + ")."
            );
        }
        return sesionRepository.save(sesion);
    }
}