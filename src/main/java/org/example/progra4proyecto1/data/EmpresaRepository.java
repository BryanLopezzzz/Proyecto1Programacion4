package org.example.progra4proyecto1.data;

import org.example.progra4proyecto1.logic.Empresa;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends CrudRepository<Empresa, Integer> {
    Optional<Empresa> findByUsuario_Correo(String correo);
    List<Empresa> findByUsuario_Estado(Usuario.Estado estado);
}