package com.librompaz.calificaciones;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Prueba de integración: levanta el microservicio con una base H2 en memoria y llama a los
 * endpoints reales con MockMvc (sin abrir un puerto).
 */
@SpringBootTest
@AutoConfigureMockMvc
class CalificacionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void listaLasEvaluacionesIniciales() throws Exception {
        mvc.perform(get("/api/evaluaciones").param("cursoId", "C3A").param("asignaturaId", "MAT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("EV1"));
    }

    @Test
    void flujoCrudCompleto() throws Exception {
        String evaluacion = """
                {"id":"EV-TEST","cursoId":"C3A","asignaturaId":"MAT",
                 "nombreInstrumento":"Guía de práctica","fecha":"2026-10-20"}
                """;
        mvc.perform(post("/api/evaluaciones").contentType(MediaType.APPLICATION_JSON).content(evaluacion))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreInstrumento").value("Guía de práctica"));

        mvc.perform(put("/api/evaluaciones/EV-TEST").contentType(MediaType.APPLICATION_JSON)
                        .content(evaluacion.replace("Guía de práctica", "Guía corregida")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreInstrumento").value("Guía corregida"));

        mvc.perform(put("/api/evaluaciones/EV-TEST/calificaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"registradoPor":"Docente de Matemática",
                                 "notas":[{"estudianteId":"E3A-1","nota":6.0}]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("EV-TEST_E3A-1"));

        mvc.perform(delete("/api/evaluaciones/EV-TEST")).andExpect(status().isNoContent());
        mvc.perform(get("/api/evaluaciones/EV-TEST")).andExpect(status().isNotFound());
    }

    @Test
    void rechazaUnaNotaFueraDeRango() throws Exception {
        mvc.perform(put("/api/evaluaciones/EV1/calificaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"registradoPor":"Docente de Matemática",
                                 "notas":[{"estudianteId":"E3A-1","nota":8.0}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Datos inválidos"));
    }
}
