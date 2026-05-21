package org.example.progra4proyecto1.presentation.api;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.io.InputStream;

@Controller
public class SpaController {

    // Archivos estáticos con extensión los maneja Spring Boot solo
    // Rutas sin extensión → devolver index.html directamente

    @GetMapping(value = {
            "/",
            "/login",
            "/buscar",
            "/registro/empresa",
            "/registro/oferente",
            "/empresa/**",
            "/oferente/**",
            "/admin/**"
    })
    public void spa(HttpServletResponse response) throws IOException {
        ClassPathResource index = new ClassPathResource("static/index.html");
        response.setContentType("text/html;charset=UTF-8");
        try (InputStream is = index.getInputStream()) {
            is.transferTo(response.getOutputStream());
        }
    }
}
