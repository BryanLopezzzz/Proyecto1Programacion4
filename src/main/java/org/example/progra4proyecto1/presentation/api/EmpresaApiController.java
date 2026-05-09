package org.example.progra4proyecto1.presentation.api;

import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.data.MonedaRepository;
import org.example.progra4proyecto1.logic.*;
import org.example.progra4proyecto1.service.EmpresaService;
import org.example.progra4proyecto1.service.OferenteService;
import org.example.progra4proyecto1.service.PuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/empresa")
public class EmpresaApiController {

    @Autowired private EmpresaService empresaService;
    @Autowired private PuestoService puestoService;
    @Autowired private OferenteService oferenteService;
    @Autowired private CaracteristicaRepository caracteristicaRepo;
    @Autowired private MonedaRepository monedaRepo;

    private Empresa getEmpresa(Authentication auth) {
        return empresaService.findByCorreo(auth.getName())
                .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
    }

    @GetMapping("/perfil")
    public Map<String, Object> perfil(Authentication auth) {
        Empresa e = getEmpresa(auth);
        return Map.of(
                "id",          e.getId(),
                "nombre",      e.getNombre(),
                "correo",      e.getUsuario().getCorreo(),
                "telefono",    e.getTelefono(),
                "localizacion",e.getLocalizacion(),
                "descripcion", e.getDescripcion(),
                "estado",      e.getUsuario().getEstado().name()
        );
    }

    @GetMapping("/puestos")
    public List<Map<String, Object>> misPuestos(Authentication auth) {
        return puestoService.findByEmpresa(getEmpresa(auth))
                .stream().map(this::puestoToMap).collect(Collectors.toList());
    }

    @PostMapping("/puestos")
    public ResponseEntity<?> publicarPuesto(@RequestBody Map<String, Object> body,
                                            Authentication auth) {
        try {
            Empresa empresa = getEmpresa(auth);

            Puesto p = new Puesto();
            p.setDescripcion((String) body.get("descripcion"));
            p.setSalario(new BigDecimal(body.get("salario").toString()));
            p.setTipo(Puesto.TipoPuesto.valueOf((String) body.get("tipo")));

            Integer monedaId = Integer.parseInt(body.get("monedaId").toString());

            @SuppressWarnings("unchecked")
            List<Map<String, Integer>> reqs = (List<Map<String, Integer>>) body.get("caracteristicas");

            List<Integer> ids    = reqs == null ? List.of() : reqs.stream().map(r -> r.get("id")).collect(Collectors.toList());
            List<Integer> niveles = reqs == null ? List.of() : reqs.stream().map(r -> r.get("nivel")).collect(Collectors.toList());

            puestoService.publicar(p, empresa, ids, niveles, monedaId);
            return ResponseEntity.ok(Map.of("mensaje", "Puesto publicado correctamente"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/puestos/{id}/desactivar")
    public ResponseEntity<?> desactivar(@PathVariable Integer id, Authentication auth) {
        Empresa empresa = getEmpresa(auth);
        return puestoService.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .map(p -> {
                    puestoService.desactivar(id);
                    return ResponseEntity.ok(Map.of("mensaje", "Puesto desactivado"));
                })
                .orElse(ResponseEntity.status(403).body(Map.of("error", "No autorizado")));
    }

    @GetMapping("/puestos/{id}/candidatos")
    public List<Map<String, Object>> candidatos(@PathVariable Integer id, Authentication auth) {
        Empresa empresa = getEmpresa(auth);
        Puesto puesto = puestoService.findById(id)
                .filter(p -> p.getEmpresa().getId().equals(empresa.getId()))
                .orElseThrow(() -> new RuntimeException("Puesto no encontrado o sin acceso"));

        return puestoService.buscarCandidatos(puesto).stream().map(c -> Map.<String, Object>of(
                "oferente", Map.of(
                        "id",             c.getOferente().getId(),
                        "nombre",         c.getOferente().getNombre(),
                        "primerApellido", c.getOferente().getPrimerApellido(),
                        "correo",         c.getOferente().getUsuario().getCorreo()
                ),
                "requisitosCumplidos",   c.getRequisitosCumplidos(),
                "requisitosTotal",       c.getRequisitosTotal(),
                "porcentajeCoincidencia",c.getPorcentajeCoincidencia()
        )).collect(Collectors.toList());
    }

    @GetMapping("/candidatos/{id}")
    public Map<String, Object> detalleCandidato(@PathVariable Integer id) {
        Oferente o = oferenteService.findById(id).orElseThrow();
        List<Map<String, Object>> habs = oferenteService.getHabilidades(o).stream()
                .map(h -> Map.<String, Object>of(
                        "caracteristica", h.getCaracteristica().getNombre(),
                        "nivel", h.getNivel()
                )).collect(Collectors.toList());
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
                "habilidades",    habs
        );
    }

    // ---- helper ----
    private Map<String, Object> puestoToMap(Puesto p) {
        return Map.of(
                "id",          p.getId(),
                "descripcion", p.getDescripcion(),
                "salario",     p.getSalario(),
                "moneda",      Map.of("id", p.getMoneda().getId(), "codigo", p.getMoneda().getCodigo()),
                "tipo",        p.getTipo().name(),
                "activo",      p.getActivo(),
                "caracteristicas", p.getCaracteristicas() == null ? List.of() :
                        p.getCaracteristicas().stream().map(pc -> Map.of(
                                "id",     pc.getCaracteristica().getId(),
                                "nombre", pc.getCaracteristica().getNombre(),
                                "nivel",  pc.getNivelRequerido()
                        )).collect(Collectors.toList()),
                "fechaRegistro", p.getFechaRegistro() != null ? p.getFechaRegistro().toString() : ""
        );
    }
}