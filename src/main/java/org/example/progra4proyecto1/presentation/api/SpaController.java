package org.example.progra4proyecto1.presentation.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // Rutas sin punto (no son archivos estáticos) → index.html
    @GetMapping(value = "/{path:[^\\.]*}")
    public String spa() {
        return "forward:/index.html";
    }

    // Rutas anidadas: /empresa/puestos/nuevo, /empresa/candidatos/5, etc.
    @GetMapping(value = "/{path:[^\\.]*}/**")
    public String spaDeep() {
        return "forward:/index.html";
    }
}
