package org.example.progra4proyecto1.data;

import com.una.bolsaempleo.logic.Administrador;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends CrudRepository<Administrador, Integer> {
    Optional<Administrador> findByIdentificacion(String identificacion);
}
