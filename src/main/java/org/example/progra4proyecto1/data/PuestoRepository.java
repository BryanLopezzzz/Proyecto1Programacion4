package org.example.progra4proyecto1.data;

import org.example.progra4proyecto1.logic.Empresa;
import org.example.progra4proyecto1.logic.Puesto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PuestoRepository extends CrudRepository<Puesto, Integer> {
    List<Puesto> findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto tipo);

    @Query("SELECT p FROM Puesto p WHERE p.tipo = 'PUBLICO' AND p.activo = true ORDER BY p.fechaRegistro DESC")
    List<Puesto> findAllPublicosActivos();

    @Query("SELECT p FROM Puesto p WHERE p.activo = true ORDER BY p.fechaRegistro DESC")
    List<Puesto> findAllActivos();

    List<Puesto> findByEmpresaOrderByFechaRegistroDesc(Empresa empresa);
    @Query("SELECT p FROM Puesto p WHERE MONTH(p.fechaRegistro) = :mes AND YEAR(p.fechaRegistro) = :anio")
    List<Puesto> findByMesYAnio(@Param("mes") int mes, @Param("anio") int anio);
    boolean existsByEmpresaAndDescripcionIgnoreCaseAndActivoTrue(Empresa empresa, String descripcion);

}
