package org.example.progra4proyecto1.presentation.api;

import org.example.progra4proyecto1.data.CaracteristicaRepository;
import org.example.progra4proyecto1.data.MonedaRepository;
import org.example.progra4proyecto1.logic.*;
import org.example.progra4proyecto1.service.PuestoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PublicoController {

    @Autowired private PuestoService puestoService;
    @Autowired private CaracteristicaRepository caracteristicaRepo;
    @Autowired private MonedaRepository monedaRepo;

    @GetMapping("/puestos/publicos")
    public List<Map<String, Object>> top5Publicos() {
        return puestoService.findTop5Publicos().stream()
                .map(this::puestoToMap)
                .collect(Collectors.toList());
    }

    @GetMapping("/puestos/{id}")
    public ResponseEntity<?> detalle(@PathVariable Integer id) {
        return puestoService.findById(id)
                .filter(p -> p.getTipo() == Puesto.TipoPuesto.PUBLICO && p.getActivo())
                .map(p -> ResponseEntity.ok(puestoToMap(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/puestos/buscar")
    public List<Map<String, Object>> buscar(
            @RequestParam(required = false) List<Integer> caracteristicas,
            @RequestParam(defaultValue = "false") boolean modoTodos,
            @RequestParam(required = false) Integer monedaId) {
        return puestoService.buscarPublicos(caracteristicas, modoTodos, monedaId)
                .stream().map(this::puestoToMap).collect(Collectors.toList());
    }

    @GetMapping("/caracteristicas")
    public List<Map<String, Object>> caracteristicas() {
        return caracteristicaRepo.findByPadreIsNull()
                .stream().map(this::caracToMap).collect(Collectors.toList());
    }

    @GetMapping("/monedas")
    public Iterable<org.example.progra4proyecto1.logic.Moneda> monedas() {
        return monedaRepo.findAll();
    }

    Map<String, Object> puestoToMap(Puesto p) {
        return Map.of(
                "id",          p.getId(),
                "descripcion", p.getDescripcion(),
                "salario",     p.getSalario(),
                "moneda",      Map.of("id", p.getMoneda().getId(), "codigo", p.getMoneda().getCodigo()),
                "tipo",        p.getTipo().name(),
                "empresa",     Map.of("id", p.getEmpresa().getId(), "nombre", p.getEmpresa().getNombre()),
                "caracteristicas", p.getCaracteristicas() == null ? List.of() :
                        p.getCaracteristicas().stream().map(pc -> Map.of(
                                "id",     pc.getCaracteristica().getId(),
                                "nombre", pc.getCaracteristica().getNombre(),
                                "nivel",  pc.getNivelRequerido()
                        )).collect(Collectors.toList()),
                "fechaRegistro", p.getFechaRegistro() != null ? p.getFechaRegistro().toString() : ""
        );
    }

    Map<String, Object> caracToMap(Caracteristica c) {
        List<Map<String, Object>> hijos = c.getHijos() == null ? List.of() :
                c.getHijos().stream()
                        .map(h -> Map.<String, Object>of(
                                "id",     h.getId(),
                                "nombre", h.getNombre(),
                                "hijos",  List.of()   // los dos niveles mencionados como recomendación del profe
                        ))
                        .collect(Collectors.toList());

        return Map.of(
                "id",     c.getId(),
                "nombre", c.getNombre(),
                "hijos",  hijos
        );
    }
}