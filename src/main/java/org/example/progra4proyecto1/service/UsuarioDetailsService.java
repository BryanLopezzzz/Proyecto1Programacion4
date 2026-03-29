package org.example.progra4proyecto1.service;


import org.example.progra4proyecto1.data.UsuarioRepository;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        if (usuario.getEstado() != Usuario.Estado.APROBADO) {
            throw new UsernameNotFoundException("Cuenta pendiente de aprobación");
        }
        return new User(usuario.getCorreo(), usuario.getClave(), List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
        );
    }
}
