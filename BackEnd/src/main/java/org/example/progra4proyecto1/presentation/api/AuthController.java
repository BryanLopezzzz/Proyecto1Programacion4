package org.example.progra4proyecto1.presentation.api;

import org.example.progra4proyecto1.data.AdministradorRepository;
import org.example.progra4proyecto1.data.UsuarioRepository;
import org.example.progra4proyecto1.security.JwtUtil;
import org.example.progra4proyecto1.logic.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private AdministradorRepository adminRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String clave    = body.get("clave");

        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(username);
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            if (!passwordEncoder.matches(clave, u.getClave()))
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
            if (u.getEstado() != Usuario.Estado.APROBADO)
                return ResponseEntity.status(403).body(Map.of("error", "Cuenta pendiente de aprobación"));

            String role = "ROLE_" + u.getRol().name();
            String token = jwtUtil.generateToken(u.getCorreo(), role);
            return ResponseEntity.ok(Map.of("token", token, "role", role, "correo", u.getCorreo()));
        }

        var adminOpt = adminRepo.findByIdentificacion(username);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            if (!passwordEncoder.matches(clave, admin.getClave()))
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));

            String token = jwtUtil.generateToken(admin.getIdentificacion(), "ROLE_ADMIN");
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role",  "ROLE_ADMIN",
                    "correo", admin.getIdentificacion()
            ));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Usuario no encontrado"));
    }
}