package org.example.progra4proyecto1.service;

import org.example.progra4proyecto1.data.AdministradorRepository;
import org.example.progra4proyecto1.data.UsuarioRepository;
import org.example.progra4proyecto1.logic.Administrador;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;


    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {

        // el flujo de esta parte es que primero se intenta ingresar como un usuario normal (empresa/oferente) usando el correo correspondiente
        Optional<Usuario> usuarioOpc = usuarioRepository.findByCorreo(input);
        if (usuarioOpc.isPresent()) {
            Usuario usuario = usuarioOpc.get();

            if (usuario.getEstado() != Usuario.Estado.APROBADO) {
                throw new UsernameNotFoundException("Cuenta pendiente de aprobación");
            }
            return new User(
                    usuario.getCorreo(),
                    usuario.getClave(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
            );
        }

        // este bloque es para cumplir con la condición del enunciado de que admin ingrese con id y clave, por ende aqui es lo que realiza la
        //condición
        Optional<Administrador> adminOpc = administradorRepository.findByIdentificacion(input);

        if (adminOpc.isPresent()) {
            Administrador admin = adminOpc.get();
            return new User(
                    admin.getIdentificacion(),
                    admin.getClave(),
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        throw new UsernameNotFoundException("Usuario no encontrado");
    }
}
