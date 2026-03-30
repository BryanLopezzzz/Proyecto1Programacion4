package org.example.progra4proyecto1.presentation;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.nio.file.Paths;

//este controller sirve para los archivos pdf que los oferentes suben como el CV
@Controller
public class ArchivoController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @GetMapping("/uploads/{nombrearchivo:.+}")
    public ResponseEntity<Resource> servirArchivo(@PathVariable String nombrearchivo) {
        try {
            Path ruta = Paths.get(uploadDir).resolve(nombrearchivo).normalize();
            Resource resource = new UrlResource(ruta.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombrearchivo + "\"").body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}