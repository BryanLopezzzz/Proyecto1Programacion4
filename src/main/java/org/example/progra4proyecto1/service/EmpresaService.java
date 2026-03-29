package org.example.progra4proyecto1.service;

import org.example.progra4proyecto1.data.*;
import org.example.progra4proyecto1.logic.Empresa;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service("empresaService")
public class EmpresaService {

    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public void registrar(Empresa empresa, String correo, String clave) {
        Optional<Usuario> existente = usuarioRepository.findByCorreo(correo);
        if (existente.isPresent()) {
            if (existente.get().getEstado() == Usuario.Estado.RECHAZADO) {
                throw new IllegalArgumentException("Este correo fue rechazado previamente. Contacte al administrador.");
            }
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(correo);
        usuario.setClave(passwordEncoder.encode(clave));
        usuario.setRol(Usuario.Rol.EMPRESA);
        usuario.setEstado(Usuario.Estado.PENDIENTE);
        usuarioRepository.save(usuario);

        empresa.setUsuario(usuario);
        empresaRepository.save(empresa);
    }

    public Optional<Empresa> findByCorreo(String correo) {
        return empresaRepository.findByUsuario_Correo(correo);
    }

    public Optional<Empresa> findById(Integer id) {
        return empresaRepository.findById(id);
    }

    public Iterable<Empresa> findAll() {
        return empresaRepository.findAll();
    }
}