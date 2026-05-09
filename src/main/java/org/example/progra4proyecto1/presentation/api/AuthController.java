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
    //en esta clase cumplimos con el PASO 1 y 2 mencionado en la
    // la diapositiva de JWT en "¿Cómo funciona JWT?"

    //1- Envío y Validación de Credenciales: El usuario envía su nombre de
    //usuario y contraseña que el servidor valida para autenticarlo correctamente.

    //2- Generación y Entrega del Token: El servidor genera un JWT con
    //información del usuario y lo envía al cliente para almacenamiento seguro.

    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private AdministradorRepository adminRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        // 1- El usuario manda username y clave
        String username = body.get("username");
        String clave    = body.get("clave");

        Optional<Usuario> usuarioOpt = usuarioRepo.findByCorreo(username);
        if (usuarioOpt.isPresent()) {
            Usuario u = usuarioOpt.get();
            // 1- El servidor valida contra la BD
            if (!passwordEncoder.matches(clave, u.getClave()))
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));
            if (u.getEstado() != Usuario.Estado.APROBADO)
                return ResponseEntity.status(403).body(Map.of("error", "Cuenta pendiente de aprobación"));

            String role = "ROLE_" + u.getRol().name();
            //2- Servidor genera el JWT y lo devuelve al cliente
            String token = jwtUtil.generateToken(u.getCorreo(), role);
            return ResponseEntity.ok(Map.of("token", token, "role", role, "correo", u.getCorreo()));
        }

        var adminOpt = adminRepo.findByIdentificacion(username);
        if (adminOpt.isPresent()) {
            var admin = adminOpt.get();
            if (!passwordEncoder.matches(clave, admin.getClave()))
                return ResponseEntity.status(401).body(Map.of("error", "Credenciales incorrectas"));

            //2- Aqui lo mismo
            //El administrador no pasa por usuario porque no esta así en SQL
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