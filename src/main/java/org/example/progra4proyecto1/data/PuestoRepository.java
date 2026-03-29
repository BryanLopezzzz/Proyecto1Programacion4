package org.example.progra4proyecto1.data;

import org.example.progra4proyecto1.logic.Empresa;
import org.example.progra4proyecto1.logic.Puesto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PuestoRepository extends CrudRepository<Puesto, Integer> {
    List<Puesto> findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto tipo);
    List<Puesto> findByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto tipo);
    List<Puesto> findByActivoTrueOrderByFechaRegistroDesc();
    List<Puesto> findByEmpresaOrderByFechaRegistroDesc(Empresa empresa);
    boolean existsByEmpresaAndDescripcionIgnoreCaseAndActivoTrue(Empresa empresa, String descripcion);

}
