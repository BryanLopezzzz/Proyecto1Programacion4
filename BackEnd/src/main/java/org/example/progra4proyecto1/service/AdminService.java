package org.example.progra4proyecto1.service;

import org.example.progra4proyecto1.data.*;
import org.example.progra4proyecto1.logic.Caracteristica;
import org.example.progra4proyecto1.logic.Empresa;
import org.example.progra4proyecto1.logic.Oferente;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service("adminService")
public class AdminService {

    @Autowired
    private UsuarioRepository usuRepo;
    @Autowired private EmpresaRepository empreRepo;
    @Autowired
    private OferenteRepository ofeRepo;
    @Autowired private CaracteristicaRepository caraRepo;

    @Transactional
    public void aprobarUsuario(Integer usuID) {
        usuRepo.findById(usuID).ifPresent(u -> {
            u.setEstado(Usuario.Estado.APROBADO);
            usuRepo.save(u);
        });
    }

    @Transactional
    public void rechazarUsuario(Integer usuarioId) {
        usuRepo.findById(usuarioId).ifPresent(u -> {
            u.setEstado(Usuario.Estado.RECHAZADO);
            usuRepo.save(u); });
    }

    public Iterable<Empresa> empresasPendientes() { return empreRepo.findByUsuario_Estado(Usuario.Estado.PENDIENTE);}

    public Iterable<Oferente> oferentesPendientes() { return ofeRepo.findByUsuario_Estado(Usuario.Estado.PENDIENTE);}

    public List<Caracteristica> raices() {
        return caraRepo.findByPadreIsNull();
    }

    public Optional<Caracteristica> findCaracteristica(Integer id) {
        return caraRepo.findById(id);
    }

    public List<Caracteristica> hijosDe(Integer padreId) {
        return caraRepo.findByPadreId(padreId);
    }

    public List<Caracteristica> todosLosNodos() {
        return caraRepo.findAllByOrderByNombreAsc();
    }

    @Transactional
    public void crearCaracteristica(String nombre, Integer padreId) {
            if (nombre == null || nombre.trim().isEmpty())
                throw new IllegalArgumentException("El nombre es requerido");

        if (padreId != null) {
            boolean yaExiste = caraRepo.findByPadreId(padreId)
                    .stream()
                    .anyMatch(h -> h.getNombre().equalsIgnoreCase(nombre.trim()));
            if (yaExiste)
                throw new IllegalArgumentException(
                        "Ya existe una característica con ese nombre en esta categoría");
        } else {
            boolean yaExiste = caraRepo.findByPadreIsNull()
                    .stream()
                    .anyMatch(r -> r.getNombre().equalsIgnoreCase(nombre.trim()));
            if (yaExiste)
                throw new IllegalArgumentException(
                        "Ya existe una categoría raíz con ese nombre");
        }

            Caracteristica c = new Caracteristica();
            c.setNombre(nombre.trim());

            if (padreId != null) {
                Caracteristica padre = caraRepo.findById(padreId)
                        .orElseThrow(() -> new IllegalArgumentException("Padre no encontrado"));

                // el error que menciono el profe corregido, estandarizar todo a 2 niveles (hijos y padre)
                if (padre.getPadre() != null)
                    throw new IllegalArgumentException(
                            "No se puede agregar un hijo a '" + padre.getNombre() +
                                    "' porque ya es un nodo hijo. Solo se permiten dos niveles.");

                c.setPadre(padre);
            }
            caraRepo.save(c);
    }
}