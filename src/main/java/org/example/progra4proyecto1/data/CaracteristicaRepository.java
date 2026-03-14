package org.example.progra4proyecto1.data;

import com.una.bolsaempleo.logic.Caracteristica;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaracteristicaRepository extends CrudRepository<Caracteristica, Integer> {
    List<Caracteristica> findByPadreIsNull();
    List<Caracteristica> findByPadreId(Integer padreId);
}
