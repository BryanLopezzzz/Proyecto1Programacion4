package org.example.progra4proyecto1.logic;


import org.example.progra4proyecto1.data.*;
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
        Optional<Usuario> existente = usuarioRepository.findByCorreo(correo);
        if (existente.isPresent()) {
            if (existente.get().getEstado() == Usuario.Estado.RECHAZADO) {
                throw new IllegalArgumentException("Este correo fue rechazado previamente. Contacte al administrador.");
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
    public void agregarHabilidad(Oferente oferente, Integer caracteristicaId, Integer nivel) {
        Caracteristica c = caracteristicaRepository.findById(caracteristicaId)
                .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada"));

        // Primero setear la caracteristica y el oferente ANTES del ID
        OferenteHabilidad h = habilidadRepository
                .findByOferenteAndCaracteristica_Id(oferente, caracteristicaId)
                .orElse(new OferenteHabilidad());

        h.setOferente(oferente);
        h.setCaracteristica(c);
        h.setNivel(nivel);

        // Construir el ID DESPUÉS de setear las entidades
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