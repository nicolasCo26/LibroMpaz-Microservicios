package com.librompaz.anotaciones.config;

import com.librompaz.anotaciones.model.Anotacion;
import com.librompaz.anotaciones.model.TipoAnotacion;
import com.librompaz.anotaciones.repository.AnotacionRepository;
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

    private final AnotacionRepository anotaciones;

    public DatosIniciales(AnotacionRepository anotaciones) {
        this.anotaciones = anotaciones;
    }

    @Override
    public void run(String... args) {
        if (anotaciones.count() > 0) return;

        anotaciones.saveAll(List.of(
                new Anotacion("AN1", "E3A-1", TipoAnotacion.POSITIVA,
                        "Ayuda a sus compañeros durante la actividad.", "2026-09-30 10:15:00", DOCENTE_MAT),
                new Anotacion("AN2", "E3A-2", TipoAnotacion.NEGATIVA,
                        "Interrumpe la clase en reiteradas ocasiones.", "2026-10-01 11:40:00", DOCENTE_MAT),
                new Anotacion("AN3", "E3A-2", TipoAnotacion.NEGATIVA,
                        "No respeta los turnos de habla.", "2026-10-05 10:20:00", DOCENTE_MAT),
                new Anotacion("AN4", "E4A-3", TipoAnotacion.OBSERVACION,
                        "Se observa desmotivado durante la lectura.", "2026-10-02 09:55:00", DOCENTE_LEN)));
    }
}
