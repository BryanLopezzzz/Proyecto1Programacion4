package org.example.progra4proyecto1.presentation.api;

import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.logic.*;
import org.example.progra4proyecto1.service.OferenteService;
import org.example.progra4proyecto1.service.PuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/oferente")
public class OferenteApiController {

    @Autowired private OferenteService oferenteService;
    @Autowired private PuestoService puestoService;
    @Autowired private CaracteristicaRepository caracteristicaRepo;

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Oferente getOferente(Authentication auth) {
        return oferenteService.findByCorreo(auth.getName()).orElseThrow();
    }

    @GetMapping("/perfil")
    public Map<String, Object> perfil(Authentication auth) {
        Oferente o = getOferente(auth);
        return Map.of(
                "id",             o.getId(),
                "nombre",         o.getNombre(),
                "primerApellido", o.getPrimerApellido(),
                "identificacion", o.getIdentificacion(),
                "correo",         o.getUsuario().getCorreo(),
                "telefono",       o.getTelefono(),
                "residencia",     o.getResidencia(),
                "nacionalidad",   o.getNacionalidad(),
                "curriculumPdf",  o.getCurriculumPdf() != null ? o.getCurriculumPdf() : "",
                "estado",         o.getUsuario().getEstado().name()
        );
    }

    @GetMapping("/habilidades")
    public List<Map<String, Object>> habilidades(Authentication auth) {
        return oferenteService.getHabilidades(getOferente(auth)).stream()
                .map(h -> Map.<String, Object>of(
                        "caracteristicaId",     h.getCaracteristica().getId(),
                        "caracteristicaNombre", h.getCaracteristica().getNombre(),
                        "nivel",                h.getNivel()
                )).collect(Collectors.toList());
    }

    @PostMapping("/habilidades")
    public ResponseEntity<?> agregarHabilidad(@RequestBody Map<String, Integer> body,
                                              Authentication auth) {
        try {
            oferenteService.agregarHabilidad(getOferente(auth),
                    body.get("caracteristicaId"), body.get("nivel"));
            return ResponseEntity.ok(Map.of("mensaje", "Habilidad guardada"));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @DeleteMapping("/habilidades/{caracteristicaId}")
    public ResponseEntity<?> eliminarHabilidad(@PathVariable Integer caracteristicaId,
                                               Authentication auth) {
        oferenteService.eliminarHabilidad(getOferente(auth), caracteristicaId);
        return ResponseEntity.ok(Map.of("mensaje", "Habilidad eliminada"));
    }

    @PostMapping("/cv")
    public ResponseEntity<?> subirCv(@RequestParam("archivo") MultipartFile archivo,
                                     Authentication auth) {
        if (archivo.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Archivo vacío"));

        String fn = archivo.getOriginalFilename();
        if (fn == null || !fn.toLowerCase().endsWith(".pdf"))
            return ResponseEntity.badRequest().body(Map.of("error", "Solo PDF"));

        try {
            Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String nombre = UUID.randomUUID() + "_" + fn;
            archivo.transferTo(dir.resolve(nombre).toFile());
            Oferente o = getOferente(auth);
            o.setCurriculumPdf(nombre);
            oferenteService.guardar(o);
            return ResponseEntity.ok(Map.of("archivo", nombre));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/puestos/buscar")
    public List<Map<String, Object>> buscarPuestosConScore(
            @RequestParam(required = false) List<Integer> caracteristicas,
            @RequestParam(defaultValue = "false") boolean modoTodos,
            Authentication auth) {

        Oferente oferente = getOferente(auth);

        return puestoService.buscarPuestosConScore(oferente, caracteristicas, modoTodos)
                .stream().map(r -> {
                    Puesto p = r.getPuesto();

                    List<Map<String, Object>> coincidencias = r.getCoincidencias().stream()
                            .map(d -> Map.<String, Object>of(
                                    "caracteristica", d.getNombreCaracteristica(),
                                    "nivelRequerido", d.getNivelRequerido(),
                                    "nivelOferente",  d.getNivelOferente() != null ? d.getNivelOferente() : -1,
                                    "peso",           d.getPeso(),
                                    "aporte",         d.getAporte(),
                                    "estado",         d.getEstado().name()
                            )).collect(Collectors.toList());

                    Map<String, Object> entry = new LinkedHashMap<>();
                    entry.put("id",                    p.getId());
                    entry.put("descripcion",           p.getDescripcion());
                    entry.put("salario",               p.getSalario());
                    entry.put("moneda",                Map.of("codigo", p.getMoneda().getCodigo()));
                    entry.put("tipo",                  p.getTipo().name());
                    entry.put("empresa",               Map.of("nombre", p.getEmpresa().getNombre()));
                    entry.put("scoreTotal",            r.getScoreTotal());
                    entry.put("porcentajeCoincidencia", r.getPorcentajeCoincidencia());
                    entry.put("porcentajeCumplidos",   r.getPorcentajeCumplidos() * 100);
                    entry.put("nivelExcedenteTotal",   r.getNivelExcedenteTotal());
                    entry.put("requisitosCumplidos",   r.getRequisitosCumplidos());
                    entry.put("requisitosTotal",       r.getRequisitosTotal());
                    entry.put("coincidencias",         coincidencias);
                    return entry;
                }).collect(Collectors.toList());
    }
}