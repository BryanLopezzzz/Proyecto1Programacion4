package org.example.progra4proyecto1.data;

import org.example.progra4proyecto1.logic.Oferente;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OferenteRepository extends CrudRepository<Oferente, Integer> {
    Optional<Oferente> findByUsuario(Usuario usuario);
    Optional<Oferente> findByUsuario_Correo(String correo);
    boolean existsByIdentificacion(String identificacion);
    List<Oferente> findByUsuario_Estado(Usuario.Estado estado);
}