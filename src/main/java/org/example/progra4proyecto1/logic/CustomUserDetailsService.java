package org.example.progra4proyecto1.logic;


import org.example.progra4proyecto1.data.UsuarioRepository;
import org.example.progra4proyecto1.data.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(username);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getEstado() != Usuario.Estado.APROBADO) {
                throw new UsernameNotFoundException("Cuenta pendiente de aprobación");
            }
            return new User(
                    usuario.getCorreo(),
                    usuario.getClave(),
                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()))
            );
        }

        // si no encontró por correo, busca admin por identificación
        return administradorRepository.findByIdentificacion(username)
                .map(admin -> {
                    Usuario u = usuarioRepository.findByCorreo("admin@bolsaempleo.com")
                            .orElseThrow(() -> new UsernameNotFoundException("Admin no encontrado"));
                    return (UserDetails) new User(
                            u.getCorreo(),
                            u.getClave(),
                            List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }
}
