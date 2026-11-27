package com.librompaz.anotaciones.repository;

import com.librompaz.anotaciones.model.Anotacion;
import com.librompaz.anotaciones.model.TipoAnotacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA genera la implementación a partir del nombre de cada método. */
public interface AnotacionRepository extends JpaRepository<Anotacion, String> {

    List<Anotacion> findAllByOrderByFechaHoraDesc();

    List<Anotacion> findByEstudianteIdOrderByFechaHoraDesc(String estudianteId);

    List<Anotacion> findByTipoOrderByFechaHoraDesc(TipoAnotacion tipo);
}
