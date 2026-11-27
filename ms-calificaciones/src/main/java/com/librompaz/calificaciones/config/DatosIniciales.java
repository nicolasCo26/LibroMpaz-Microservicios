package com.librompaz.calificaciones.config;

import com.librompaz.calificaciones.model.Calificacion;
import com.librompaz.calificaciones.model.Evaluacion;
import com.librompaz.calificaciones.repository.CalificacionRepository;
import com.librompaz.calificaciones.repository.EvaluacionRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Datos FICTICIOS iniciales (los mismos ids que usa la app). Solo se insertan si la base está
 * vacía, es decir, la primera vez que se levanta el microservicio.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final String DOCENTE_MAT = "Docente de Matemática";
    private static final String DOCENTE_LEN = "Docente de Lenguaje";

    private final EvaluacionRepository evaluaciones;
    private final CalificacionRepository calificaciones;

    public DatosIniciales(EvaluacionRepository evaluaciones, CalificacionRepository calificaciones) {
        this.evaluaciones = evaluaciones;
        this.calificaciones = calificaciones;
    }

    @Override
    public void run(String... args) {
        if (evaluaciones.count() > 0) return;

        evaluaciones.saveAll(List.of(
                new Evaluacion("EV1", "C3A", "MAT", "Prueba unidad 1", "2026-09-25"),
                new Evaluacion("EV2", "C4A", "LEN", "Control de lectura", "2026-09-30")));

        // EV1 queda incompleta a propósito (2 de 8 notas) para que UTP vea una omisión.
        List<Calificacion> notas = new ArrayList<>(List.of(
                new Calificacion("EV1", "E3A-1", 6.2, DOCENTE_MAT),
                new Calificacion("EV1", "E3A-2", 4.5, DOCENTE_MAT)));
        double[] notasEv2 = {5.5, 6.0, 4.8, 6.7, 3.9, 5.1, 6.3, 5.8};
        for (int i = 0; i < notasEv2.length; i++) {
            notas.add(new Calificacion("EV2", "E4A-" + (i + 1), notasEv2[i], DOCENTE_LEN));
        }
        calificaciones.saveAll(notas);
    }
}
