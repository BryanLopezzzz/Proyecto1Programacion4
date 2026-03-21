package org.example.progra4proyecto1.data;

import org.example.progra4proyecto1.logic.Caracteristica;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaracteristicaRepository extends CrudRepository<Caracteristica, Integer> {

    /** Nodos raíz (sin padre) — para mostrar el árbol en la UI */
    List<Caracteristica> findByPadreIsNull();

    /** Hijos directos de un nodo dado */
    List<Caracteristica> findByPadreId(Integer padreId);

    /**
     * Todos los nodos ordenados por nombre.
     * Necesario para poblar el select de "padre" al crear una característica:
     * así se puede asignar cualquier nodo como padre (no solo raíces),
     * permitiendo el árbol de 3+ niveles que requiere el proyecto.
     */
    List<Caracteristica> findAllByOrderByNombreAsc();
}