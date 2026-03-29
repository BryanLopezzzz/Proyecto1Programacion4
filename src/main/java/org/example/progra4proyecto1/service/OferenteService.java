package org.example.progra4proyecto1.service;

import org.example.progra4proyecto1.data.*;
import org.example.progra4proyecto1.logic.Caracteristica;
import org.example.progra4proyecto1.logic.Oferente;
import org.example.progra4proyecto1.logic.OferenteHabilidad;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service("oferenteService")
public class OferenteService {
    @Autowired private OferenteRepository ofeRepo;
    @Autowired private UsuarioRepository usuRepo;
    @Autowired private OferenteHabilidadRepository habilidadRepo;
    @Autowired private CaracteristicaRepository caraRepo;
    @Autowired private PasswordEncoder passEncoder;

    @Transactional
    public void registrar(Oferente oferente, String correo, String clave) {
        Optional<Usuario> exis = usuRepo.findByCorreo(correo);
        if (exis.isPresent()) {
            if (exis.get().getEstado() == Usuario.Estado.RECHAZADO) {
                throw new IllegalArgumentException("Este correo fue rechazado previamente.");
            }
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        if (ofeRepo.existsByIdentificacion(oferente.getIdentificacion()))
            throw new IllegalArgumentException("La identificación ya está registrada");
        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setClave(passEncoder.encode(clave));
        usuario.setRol(Usuario.Rol.OFERENTE);
        usuario.setEstado(Usuario.Estado.PENDIENTE);
        usuRepo.save(usuario);
        oferente.setUsuario(usuario);
        ofeRepo.save(oferente);
    }
    public Optional<Oferente> findByCorreo(String correo) {
        return ofeRepo.findByUsuario_Correo(correo);
    }
    public Optional<Oferente> findById(Integer id) {
        return ofeRepo.findById(id);
    }

    @Transactional
    public void agregarHabilidad(Oferente oferente, Integer caraID, Integer nivel) {
        Caracteristica c = caraRepo.findById(caraID).orElseThrow(() -> new IllegalArgumentException("Característica no encontrada"));
        OferenteHabilidad h = habilidadRepo.findByOferenteAndCaracteristica_Id(oferente, caraID).orElse(new OferenteHabilidad());
        h.setOferente(oferente);
        h.setCaracteristica(c);
        h.setNivel(nivel);
        OferenteHabilidad.OferenteHabilidadId hId = new OferenteHabilidad.OferenteHabilidadId();
        hId.setOferenteId(oferente.getId());
        hId.setCaracteristicaId(c.getId());
        h.setId(hId);
        habilidadRepo.save(h);
    }
    @Transactional
    public void eliminarHabilidad(Oferente oferente, Integer caraID) {
        habilidadRepo.deleteByOferenteAndCaracteristica_Id(oferente, caraID);
    }
    public List<OferenteHabilidad> getHabilidades(Oferente oferente) {
        return habilidadRepo.findByOferente(oferente);
    }
    public void guardar(Oferente oferente) {
        ofeRepo.save(oferente);
    }
}