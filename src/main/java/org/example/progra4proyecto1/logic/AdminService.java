package org.example.progra4proyecto1.logic;

import org.example.progra4proyecto1.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service("adminService")
public class AdminService {

    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private OferenteRepository oferenteRepository;
    @Autowired private CaracteristicaRepository caracteristicaRepository;

    @Transactional
    public void aprobarUsuario(Integer usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(u -> {
            u.setEstado(Usuario.Estado.APROBADO);
            usuarioRepository.save(u);
        });
    }

    @Transactional
    public void rechazarUsuario(Integer usuarioId) {
        usuarioRepository.findById(usuarioId).ifPresent(u -> {
            u.setEstado(Usuario.Estado.RECHAZADO);
            usuarioRepository.save(u);
        });
    }

    public Iterable<Empresa> empresasPendientes() {
        return empresaRepository.findByUsuario_Estado(Usuario.Estado.PENDIENTE);
    }

    public Iterable<Oferente> oferentesPendientes() {
        return oferenteRepository.findByUsuario_Estado(Usuario.Estado.PENDIENTE);
    }

    public List<Caracteristica> raices() { return caracteristicaRepository.findByPadreIsNull(); }
    public Optional<Caracteristica> findCaracteristica(Integer id) { return caracteristicaRepository.findById(id); }
    public List<Caracteristica> hijosDe(Integer padreId) { return caracteristicaRepository.findByPadreId(padreId); }

    @Transactional
    public void crearCaracteristica(String nombre, Integer padreId) {
        Caracteristica c = new Caracteristica();
        c.setNombre(nombre);
        if (padreId != null)
            caracteristicaRepository.findById(padreId).ifPresent(c::setPadre);
        caracteristicaRepository.save(c);
    }
}
