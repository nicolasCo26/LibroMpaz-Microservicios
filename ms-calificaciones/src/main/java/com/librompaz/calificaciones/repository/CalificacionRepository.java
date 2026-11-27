package com.librompaz.calificaciones.repository;

import com.librompaz.calificaciones.model.Calificacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalificacionRepository extends JpaRepository<Calificacion, String> {

    List<Calificacion> findByEvaluacionId(String evaluacionId);

    /** Borrado masivo: se ejecuta de inmediato en la base, antes de insertar la planilla nueva. */
    @Modifying
    @Query("DELETE FROM Calificacion c WHERE c.evaluacionId = :evaluacionId")
    void eliminarDeEvaluacion(@Param("evaluacionId") String evaluacionId);
}
