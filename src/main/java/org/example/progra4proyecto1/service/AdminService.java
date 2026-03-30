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
        Caracteristica c = new Caracteristica();
        c.setNombre(nombre);
        if (padreId != null) {
            caraRepo.findById(padreId).ifPresent(c::setPadre);
        }
        caraRepo.save(c);
    }
}