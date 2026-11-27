package com.librompaz.calificaciones.repository;

import com.librompaz.calificaciones.model.Evaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA genera la implementación (findAll, save, delete...) a partir de la interfaz. */
public interface EvaluacionRepository extends JpaRepository<Evaluacion, String> {

    List<Evaluacion> findAllByOrderByFechaDesc();

    List<Evaluacion> findByCursoIdAndAsignaturaIdOrderByFechaDesc(String cursoId, String asignaturaId);
}
