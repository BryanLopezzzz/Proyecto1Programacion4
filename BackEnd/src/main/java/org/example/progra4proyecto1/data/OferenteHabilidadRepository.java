package org.example.progra4proyecto1.data;

import org.example.progra4proyecto1.logic.Oferente;
import org.example.progra4proyecto1.logic.OferenteHabilidad;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OferenteHabilidadRepository extends CrudRepository<OferenteHabilidad, OferenteHabilidad.OferenteHabilidadId> {
    List<OferenteHabilidad> findByOferente(Oferente oferente);
    Optional<OferenteHabilidad> findByOferenteAndCaracteristica_Id(Oferente oferente, Integer caracteristicaId);
    void deleteByOferenteAndCaracteristica_Id(Oferente oferente, Integer caracteristicaId);
}