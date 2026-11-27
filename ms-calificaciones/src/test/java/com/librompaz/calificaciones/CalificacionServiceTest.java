package com.librompaz.calificaciones;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.librompaz.calificaciones.dto.EvaluacionRequest;
import com.librompaz.calificaciones.dto.GuardarNotasRequest;
import com.librompaz.calificaciones.dto.NotaRequest;
import com.librompaz.calificaciones.exception.ConflictoException;
import com.librompaz.calificaciones.exception.RecursoNoEncontradoException;
import com.librompaz.calificaciones.model.Calificacion;
import com.librompaz.calificaciones.model.Evaluacion;
import com.librompaz.calificaciones.repository.CalificacionRepository;
import com.librompaz.calificaciones.repository.EvaluacionRepository;
import com.librompaz.calificaciones.service.CalificacionService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Prueba unitaria del servicio: los repositorios se simulan con Mockito (no hay base de datos). */
class CalificacionServiceTest {

    private EvaluacionRepository evaluaciones;
    private CalificacionRepository calificaciones;
    private CalificacionService servicio;

    @BeforeEach
    void preparar() {
        evaluaciones = mock(EvaluacionRepository.class);
        calificaciones = mock(CalificacionRepository.class);
        servicio = new CalificacionService(evaluaciones, calificaciones);
        when(evaluaciones.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));
    }

    @Test
    void crearSinIdGeneraUnUuid() {
        Evaluacion creada = servicio.crearEvaluacion(
                new EvaluacionRequest(null, "C3A", "MAT", "  Prueba 2  ", "2026-10-10"));

        assertThat(creada.getId()).hasSize(36);
        assertThat(creada.getNombreInstrumento()).isEqualTo("Prueba 2");
    }

    @Test
    void crearConIdRepetidoLanzaConflicto() {
        when(evaluaciones.existsById("EV1")).thenReturn(true);

        assertThatThrownBy(() -> servicio.crearEvaluacion(
                new EvaluacionRequest("EV1", "C3A", "MAT", "Prueba", "2026-10-10")))
                .isInstanceOf(ConflictoException.class);
        verify(evaluaciones, never()).save(any());
    }

    @Test
    void obtenerUnaEvaluacionInexistenteLanza404() {
        when(evaluaciones.findById("X")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtenerEvaluacion("X"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void eliminarBorraPrimeroLasNotas() {
        Evaluacion ev = new Evaluacion("EV1", "C3A", "MAT", "Prueba", "2026-09-25");
        when(evaluaciones.findById("EV1")).thenReturn(Optional.of(ev));

        servicio.eliminarEvaluacion("EV1");

        verify(calificaciones).eliminarDeEvaluacion("EV1");
        verify(evaluaciones).delete(ev);
    }

    @Test
    void guardarNotasReemplazaLaPlanilla() {
        when(evaluaciones.findById("EV1"))
                .thenReturn(Optional.of(new Evaluacion("EV1", "C3A", "MAT", "Prueba", "2026-09-25")));
        when(calificaciones.saveAll(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

        List<Calificacion> guardadas = servicio.guardarNotas("EV1", new GuardarNotasRequest(
                "Docente de Matemática", List.of(new NotaRequest("E3A-1", 6.5))));

        verify(calificaciones).eliminarDeEvaluacion("EV1");
        assertThat(guardadas).hasSize(1);
        assertThat(guardadas.get(0).getId()).isEqualTo("EV1_E3A-1");
    }
}
