package org.example.progra4proyecto1.presentation.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Reenvía todas las rutas no-API al index.html de la SPA.
 * Spring Boot sirve /static/index.html automáticamente en "/",
 * pero rutas como /login, /buscar, etc. necesitan este fallback.
 */
@Controller
public class SpaController {

    @GetMapping(value = {
            "/", "/login", "/buscar",
            "/registro/empresa", "/registro/oferente",
            "/empresa/**", "/oferente/**", "/admin/**"
    })
    public String spa() {
        return "forward:/index.html";
    }
}