package com.librompaz.anotaciones.service;

import com.librompaz.anotaciones.dto.AnotacionRequest;
import com.librompaz.anotaciones.exception.ConflictoException;
import com.librompaz.anotaciones.exception.NoAutorizadoException;
import com.librompaz.anotaciones.exception.RecursoNoEncontradoException;
import com.librompaz.anotaciones.model.Anotacion;
import com.librompaz.anotaciones.model.TipoAnotacion;
import com.librompaz.anotaciones.repository.AnotacionRepository;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Lógica de negocio de la hoja de vida. */
@Service
public class AnotacionService {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final AnotacionRepository anotaciones;
    private final Clock reloj;

    @Autowired
    public AnotacionService(AnotacionRepository anotaciones) {
        this(anotaciones, Clock.system(ZoneId.of("America/Santiago")));
    }

    /** Las pruebas pasan un reloj fijo para conocer la marca temporal exacta. */
    public AnotacionService(AnotacionRepository anotaciones, Clock reloj) {
        this.anotaciones = anotaciones;
        this.reloj = reloj;
    }

    /** Filtra por estudiante o por tipo si vienen; si no, devuelve todas. */
    public List<Anotacion> listar(String estudianteId, TipoAnotacion tipo) {
        if (estudianteId != null) return anotaciones.findByEstudianteIdOrderByFechaHoraDesc(estudianteId);
        if (tipo != null) return anotaciones.findByTipoOrderByFechaHoraDesc(tipo);
        return anotaciones.findAllByOrderByFechaHoraDesc();
    }

    public Anotacion obtener(String id) {
        return anotaciones.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la anotación " + id));
    }

    /** La marca temporal la pone el servidor: el teléfono no puede cambiar la hora de la firma. */
    public Anotacion crear(AnotacionRequest datos) {
        String id = (datos.id() == null || datos.id().isBlank()) ? UUID.randomUUID().toString() : datos.id();
        if (anotaciones.existsById(id)) {
            throw new ConflictoException("Ya existe una anotación con id " + id);
        }
        return anotaciones.save(new Anotacion(
                id, datos.estudianteId(), datos.tipo(), datos.descripcion().trim(),
                LocalDateTime.now(reloj).format(FORMATO), datos.firmadoPor()));
    }

    /** Corrige el tipo y la descripción. La firma original (quién y cuándo) no cambia. */
    public Anotacion actualizar(String id, AnotacionRequest datos) {
        Anotacion anotacion = obtener(id);
        validarFirmante(anotacion, datos.firmadoPor());
        anotacion.setTipo(datos.tipo());
        anotacion.setDescripcion(datos.descripcion().trim());
        return anotaciones.save(anotacion);
    }

    public void eliminar(String id, String solicitante) {
        Anotacion anotacion = obtener(id);
        validarFirmante(anotacion, solicitante);
        anotaciones.delete(anotacion);
    }

    /** Regla de negocio: solo quien firmó la anotación puede corregirla o eliminarla. */
    private void validarFirmante(Anotacion anotacion, String usuario) {
        if (!anotacion.getFirmadoPor().equals(usuario)) {
            throw new NoAutorizadoException("Solo " + anotacion.getFirmadoPor() + " puede modificar esta anotación");
        }
    }
}
