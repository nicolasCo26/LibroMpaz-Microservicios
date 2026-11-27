package com.librompaz.anotaciones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.librompaz.anotaciones.dto.AnotacionRequest;
import com.librompaz.anotaciones.exception.NoAutorizadoException;
import com.librompaz.anotaciones.model.Anotacion;
import com.librompaz.anotaciones.model.TipoAnotacion;
import com.librompaz.anotaciones.repository.AnotacionRepository;
import com.librompaz.anotaciones.service.AnotacionService;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Prueba unitaria del servicio: el repositorio se simula con Mockito y el reloj queda fijo. */
class AnotacionServiceTest {

    private static final String DOCENTE = "Docente de Matemática";

    private AnotacionRepository repositorio;
    private AnotacionService servicio;

    @BeforeEach
    void preparar() {
        repositorio = mock(AnotacionRepository.class);
        Clock reloj = Clock.fixed(Instant.parse("2026-11-02T13:30:00Z"), ZoneId.of("America/Santiago"));
        servicio = new AnotacionService(repositorio, reloj);
        when(repositorio.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void crearPoneLaMarcaTemporalDelServidor() {
        Anotacion creada = servicio.crear(new AnotacionRequest(
                null, "E3A-1", TipoAnotacion.POSITIVA, "Participa activamente en clase.", DOCENTE));

        assertThat(creada.getFechaHora()).isEqualTo("2026-11-02 10:30:00");
        assertThat(creada.getFirmadoPor()).isEqualTo(DOCENTE);
    }

    @Test
    void soloQuienFirmoPuedeCorregir() {
        when(repositorio.findById("AN1")).thenReturn(Optional.of(new Anotacion(
                "AN1", "E3A-1", TipoAnotacion.POSITIVA, "Texto original de prueba.", "2026-09-30 10:15:00", DOCENTE)));

        assertThatThrownBy(() -> servicio.actualizar("AN1", new AnotacionRequest(
                null, "E3A-1", TipoAnotacion.NEGATIVA, "Texto cambiado por otra persona.", "Docente de Lenguaje")))
                .isInstanceOf(NoAutorizadoException.class);
        verify(repositorio, never()).save(any());
    }

    @Test
    void elFirmanteCorrigeTipoYDescripcion() {
        when(repositorio.findById("AN1")).thenReturn(Optional.of(new Anotacion(
                "AN1", "E3A-1", TipoAnotacion.POSITIVA, "Texto original de prueba.", "2026-09-30 10:15:00", DOCENTE)));

        Anotacion corregida = servicio.actualizar("AN1", new AnotacionRequest(
                null, "E3A-1", TipoAnotacion.OBSERVACION, "Texto corregido por el docente.", DOCENTE));

        assertThat(corregida.getTipo()).isEqualTo(TipoAnotacion.OBSERVACION);
        assertThat(corregida.getFechaHora()).isEqualTo("2026-09-30 10:15:00");
    }
}
