package org.example.progra4proyecto1.presentation.api;

import org.example.progra4proyecto1.logic.*;
import org.example.progra4proyecto1.service.EmpresaService;
import org.example.progra4proyecto1.service.OferenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/registro")
public class RegistroController {

    @Autowired private EmpresaService empresaService;
    @Autowired private OferenteService oferenteService;

    @PostMapping("/empresa")
    public ResponseEntity<?> registrarEmpresa(@RequestBody Map<String, String> body) {
        try {
            Empresa e = new Empresa();
            e.setNombre(body.get("nombre"));
            e.setLocalizacion(body.get("localizacion"));
            e.setTelefono(body.get("telefono"));
            e.setDescripcion(body.get("descripcion"));
            empresaService.registrar(e, body.get("correo"), body.get("clave"));
            return ResponseEntity.ok(Map.of("mensaje", "Empresa registrada. Espere aprobación."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/oferente")
    public ResponseEntity<?> registrarOferente(@RequestBody Map<String, String> body) {
        try {
            Oferente o = new Oferente();
            o.setIdentificacion(body.get("identificacion"));
            o.setNombre(body.get("nombre"));
            o.setPrimerApellido(body.get("primerApellido"));
            o.setNacionalidad(body.get("nacionalidad"));
            o.setTelefono(body.get("telefono"));
            o.setResidencia(body.get("residencia"));
            oferenteService.registrar(o, body.get("correo"), body.get("clave"));
            return ResponseEntity.ok(Map.of("mensaje", "Oferente registrado. Espere aprobación."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }
}