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

   //@Query("SELECT p FROM Puesto p WHERE p.tipo = 'PUBLICO' AND p.activo = true ORDER BY p.fechaRegistro DESC LIMIT 5")
   // List<Puesto> findTop5Publicos(); (Cambiamos estas lineas ante posibles problemas de RUN)
    List<Puesto> findTop5ByTipoAndActivoTrueOrderByFechaRegistroDesc(Puesto.TipoPuesto tipo);

    @Query("SELECT p FROM Puesto p WHERE p.tipo = 'PUBLICO' AND p.activo = true ORDER BY p.fechaRegistro DESC")
    List<Puesto> findAllPublicosActivos();

    @Query("SELECT p FROM Puesto p WHERE p.activo = true ORDER BY p.fechaRegistro DESC")
    List<Puesto> findAllActivos();

    List<Puesto> findByEmpresaOrderByFechaRegistroDesc(Empresa empresa);

    @Query("SELECT p FROM Puesto p WHERE MONTH(p.fechaRegistro) = :mes AND YEAR(p.fechaRegistro) = :anio")
    List<Puesto> findByMesYAnio(@Param("mes") int mes, @Param("anio") int anio);

//    @Query("SELECT COUNT(p) > 0 FROM Puesto p WHERE p.empresa = :empresa AND LOWER(p.descripcion) = LOWER(:descripcion) AND p.activo = true AND (:idExcluir IS NULL OR p.id <> :idExcluir)")
//    boolean existsDuplicado(@Param("empresa") Empresa empresa, @Param("descripcion") String descripcion, @Param("idExcluir") Integer idExcluir);
    //De igual Manera, cambiamos por consejo de problematico NULL.
    boolean existsByEmpresaAndDescripcionIgnoreCaseAndActivoTrue(Empresa empresa, String descripcion);

}
