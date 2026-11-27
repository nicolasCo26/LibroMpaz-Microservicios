package com.librompaz.anotaciones;

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

/** Prueba de integración: endpoints reales con una base H2 en memoria. */
@SpringBootTest
@AutoConfigureMockMvc
class AnotacionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void filtraPorEstudiante() throws Exception {
        mvc.perform(get("/api/anotaciones").param("estudianteId", "E3A-2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void flujoCrudCompleto() throws Exception {
        String anotacion = """
                {"id":"AN-TEST","estudianteId":"E4A-1","tipo":"POSITIVA",
                 "descripcion":"Lidera el trabajo en grupo.","firmadoPor":"Docente de Lenguaje"}
                """;
        mvc.perform(post("/api/anotaciones").contentType(MediaType.APPLICATION_JSON).content(anotacion))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fechaHora").isNotEmpty());

        mvc.perform(put("/api/anotaciones/AN-TEST").contentType(MediaType.APPLICATION_JSON)
                        .content(anotacion.replace("POSITIVA", "OBSERVACION")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("OBSERVACION"));

        mvc.perform(delete("/api/anotaciones/AN-TEST").param("solicitante", "Docente de Matemática"))
                .andExpect(status().isForbidden());

        mvc.perform(delete("/api/anotaciones/AN-TEST").param("solicitante", "Docente de Lenguaje"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/anotaciones/AN-TEST")).andExpect(status().isNotFound());
    }

    @Test
    void rechazaUnaDescripcionCorta() throws Exception {
        mvc.perform(post("/api/anotaciones").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"estudianteId":"E4A-1","tipo":"NEGATIVA",
                                 "descripcion":"Corta","firmadoPor":"Docente de Lenguaje"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.descripcion").exists());
    }
}
