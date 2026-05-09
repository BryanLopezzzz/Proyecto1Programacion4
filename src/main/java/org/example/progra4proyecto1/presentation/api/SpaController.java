package org.example.progra4proyecto1.presentation.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
            "/empresa/dashboard", "/empresa/puestos",
            "/empresa/puestos/nuevo",
            "/oferente/dashboard", "/oferente/habilidades",
            "/oferente/cv", "/oferente/puestos",
            "/admin/dashboard", "/admin/empresas",
            "/admin/oferentes", "/admin/caracteristicas",
            "/admin/reportes"
    })
    public String spa() {
        return "forward:/index.html";
    }
    @GetMapping("/empresa/candidatos/{id}")
    public String candidatoDetalle(@PathVariable String id) {
        return "forward:/index.html";
    }
}