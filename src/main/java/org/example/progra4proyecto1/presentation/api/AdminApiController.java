package org.example.progra4proyecto1.presentation.api;

import org.example.progra4proyecto1.logic.*;
import org.example.progra4proyecto1.service.AdminService;
import org.example.progra4proyecto1.service.PuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    @Autowired private AdminService adminService;
    @Autowired private PuestoService puestoService;


    @GetMapping("/empresas/pendientes")
    public List<Map<String, Object>> empresasPendientes() {
        List<Map<String, Object>> result = new ArrayList<>();
        adminService.empresasPendientes().forEach(e -> result.add(Map.of(
                "usuarioId",   e.getUsuario().getId(),
                "nombre",      e.getNombre(),
                "correo",      e.getUsuario().getCorreo(),
                "telefono",    e.getTelefono(),
                "localizacion",e.getLocalizacion(),
                "descripcion", e.getDescripcion(),
                "estado",      e.getUsuario().getEstado().name()
        )));
        return result;
    }

    @PostMapping("/empresas/{usuarioId}/aprobar")
    public ResponseEntity<?> aprobarEmpresa(@PathVariable Integer usuarioId) {
        adminService.aprobarUsuario(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Empresa aprobada"));
    }

    @PostMapping("/empresas/{usuarioId}/rechazar")
    public ResponseEntity<?> rechazarEmpresa(@PathVariable Integer usuarioId) {
        adminService.rechazarUsuario(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Empresa rechazada"));
    }


    @GetMapping("/oferentes/pendientes")
    public List<Map<String, Object>> oferentesPendientes() {
        List<Map<String, Object>> result = new ArrayList<>();
        adminService.oferentesPendientes().forEach(o -> result.add(Map.of(
                "usuarioId",      o.getUsuario().getId(),
                "nombre",         o.getNombre(),
                "primerApellido", o.getPrimerApellido(),
                "identificacion", o.getIdentificacion(),
                "correo",         o.getUsuario().getCorreo(),
                "telefono",       o.getTelefono(),
                "nacionalidad",   o.getNacionalidad(),
                "estado",         o.getUsuario().getEstado().name()
        )));
        return result;
    }

    @PostMapping("/oferentes/{usuarioId}/aprobar")
    public ResponseEntity<?> aprobarOferente(@PathVariable Integer usuarioId) {
        adminService.aprobarUsuario(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Oferente aprobado"));
    }

    @PostMapping("/oferentes/{usuarioId}/rechazar")
    public ResponseEntity<?> rechazarOferente(@PathVariable Integer usuarioId) {
        adminService.rechazarUsuario(usuarioId);
        return ResponseEntity.ok(Map.of("mensaje", "Oferente rechazado"));
    }


    @GetMapping("/caracteristicas")
    public List<Map<String, Object>> caracteristicasArbol() {
        return adminService.raices().stream().map(r -> {
            List<Map<String, Object>> hijos = adminService.hijosDe(r.getId())
                    .stream()
                    .map(h -> Map.<String, Object>of(
                            "id",     h.getId(),
                            "nombre", h.getNombre(),
                            "hijos",  List.of()   // los hijos ya no tienen hijos, los dos niveles mencionados por el profe
                    ))
                    .collect(Collectors.toList());

            return Map.<String, Object>of(
                    "id",     r.getId(),
                    "nombre", r.getNombre(),
                    "hijos",  hijos
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/caracteristicas/todos")
    public List<Map<String, Object>> todosNodos() {
        return adminService.raices().stream()
                .map(c -> Map.<String, Object>of(
                        "id",         c.getId(),
                        "nombre",     c.getNombre(),
                        "tienePadre", false
                ))
                .collect(Collectors.toList());
    }

    @PostMapping("/caracteristicas")
    public ResponseEntity<?> crearCaracteristica(@RequestBody Map<String, Object> body) {
        String nombre = (String) body.get("nombre");
        Integer padreId = body.get("padreId") != null
                ? Integer.parseInt(body.get("padreId").toString()) : null;
        adminService.crearCaracteristica(nombre, padreId);
        return ResponseEntity.ok(Map.of("mensaje", "Característica creada"));
    }

    @GetMapping("/reportes/puestos")
    public List<Map<String, Object>> reportePuestos(@RequestParam int mes,
                                                    @RequestParam int anio) {
        return puestoService.findByMesYAnio(mes, anio).stream().map(p -> Map.<String, Object>of(
                "id",          p.getId(),
                "empresa",     p.getEmpresa().getNombre(),
                "descripcion", p.getDescripcion(),
                "salario",     p.getSalario(),
                "moneda",      p.getMoneda().getCodigo(),
                "tipo",        p.getTipo().name(),
                "fecha",       p.getFechaRegistro().toString(),
                "caracteristicas", p.getCaracteristicas() == null ? List.of() :
                        p.getCaracteristicas().stream().map(pc -> Map.of(
                                "nombre", pc.getCaracteristica().getNombre(),
                                "nivel",  pc.getNivelRequerido()
                        )).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }

    @GetMapping("/reportes/coincidencias")
    public List<Map<String, Object>> reporteCoincidencias(@RequestParam int mes,
                                                          @RequestParam int anio) {
        return puestoService.findByMesYAnio(mes, anio).stream().map(p -> {
            List<Map<String, Object>> candidatos = puestoService.buscarCandidatos(p).stream()
                    .filter(c -> c.getScoreTotal() > 0)
                    .map(c -> Map.<String, Object>of(
                            "nombre",         c.getOferente().getNombre() + " " + c.getOferente().getPrimerApellido(),
                            "identificacion", c.getOferente().getIdentificacion(),
                            "correo",         c.getOferente().getUsuario().getCorreo(),
                            "cumplidos",      c.getRequisitosCumplidos(),
                            "total",          c.getRequisitosTotal(),
                            "porcentaje",     c.getPorcentajeCoincidencia()
                    )).collect(Collectors.toList());

            return Map.<String, Object>of(
                    "puesto",     p.getDescripcion(),
                    "empresa",    p.getEmpresa().getNombre(),
                    "tipo",       p.getTipo().name(),
                    "candidatos", candidatos
            );
        }).collect(Collectors.toList());
    }
}