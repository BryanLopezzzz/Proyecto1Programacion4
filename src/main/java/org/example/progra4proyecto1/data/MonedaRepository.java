package org.example.progra4proyecto1.data;


import org.example.progra4proyecto1.logic.Moneda;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonedaRepository extends CrudRepository<Moneda, Integer> {}
