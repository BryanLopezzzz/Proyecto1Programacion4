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

    @Autowired private OferenteRepository oferenteRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private OferenteHabilidadRepository habilidadRepository;
    @Autowired private CaracteristicaRepository caracteristicaRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public void registrar(Oferente oferente, String correo, String clave) {
        Optional<Usuario> exis = usuRepo.findByCorreo(correo);
        if (exis.isPresent()) {
            if (exis.get().getEstado() == Usuario.Estado.RECHAZADO) {
                throw new IllegalArgumentException("Este correo fue rechazado previamente.");
            }
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        if (oferenteRepository.existsByIdentificacion(oferente.getIdentificacion()))
            throw new IllegalArgumentException("La identificación ya está registrada");

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRol(Usuario.Rol.OFERENTE);
        usuario.setEstado(Usuario.Estado.PENDIENTE);
        usuarioRepository.save(usuario);

        oferente.setUsuario(usuario);
        oferenteRepository.save(oferente);
    }

    public Optional<Oferente> findByCorreo(String correo) {
        return oferenteRepository.findByUsuario_Correo(correo);
    }
    public Optional<Oferente> findById(Integer id) {
        return oferenteRepository.findById(id);
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
        hId.setCaracteristicaId(c.getId()); // usar c.getId() no caracteristicaId directo
        h.setId(hId);

        habilidadRepository.save(h);
    }
    @Transactional
    public void eliminarHabilidad(Oferente oferente, Integer caracteristicaId) {
        habilidadRepository.deleteByOferenteAndCaracteristica_Id(oferente, caracteristicaId);
    }

    public List<OferenteHabilidad> getHabilidades(Oferente oferente) {
        return habilidadRepository.findByOferente(oferente);
    }

    public void guardar(Oferente oferente) {
        oferenteRepository.save(oferente);
    }
}